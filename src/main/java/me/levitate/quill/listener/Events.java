package me.levitate.quill.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Events {
    /**
     * Start building an event listener
     */
    public static <T extends Event> EventBuilder<T> listen(Plugin plugin, Class<T> eventClass) {
        return new EventBuilder<>(plugin, eventClass);
    }

    /**
     * Quick register with default priority
     */
    public static <T extends Event> void listen(Plugin plugin, Class<T> eventClass, Consumer<T> handler) {
        new EventBuilder<>(plugin, eventClass).handle(handler);
    }

    public static class EventBuilder<T extends Event> {
        private final Plugin plugin;
        private final Class<T> eventClass;
        private EventPriority priority = EventPriority.NORMAL;
        private boolean ignoreCancelled = false;
        private Predicate<T> filter;

        private EventBuilder(Plugin plugin, Class<T> eventClass) {
            this.plugin = plugin;
            this.eventClass = eventClass;
        }

        /**
         * Set the event priority
         */
        public EventBuilder<T> priority(EventPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Set to monitor priority
         */
        public EventBuilder<T> monitor() {
            return priority(EventPriority.MONITOR);
        }

        /**
         * Set to highest priority
         */
        public EventBuilder<T> highest() {
            return priority(EventPriority.HIGHEST);
        }

        /**
         * Set to lowest priority
         */
        public EventBuilder<T> lowest() {
            return priority(EventPriority.LOWEST);
        }

        /**
         * Set whether to ignore cancelled events
         */
        public EventBuilder<T> ignoreCancelled(boolean ignore) {
            this.ignoreCancelled = ignore;
            return this;
        }

        /**
         * Add a filter condition
         */
        public EventBuilder<T> filter(Predicate<T> filter) {
            if (this.filter == null) {
                this.filter = filter;
            } else {
                this.filter = this.filter.and(filter);
            }
            return this;
        }

        /**
         * Register the event handler
         */
        public void handle(Consumer<T> handler) {
            Listener listener = new Listener() {};

            Consumer<T> wrappedHandler = filter != null
                    ? event -> { if (filter.test(event)) handler.accept(event); }
                    : handler;

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass,
                    listener,
                    priority,
                    (l, event) -> {
                        if (eventClass.isInstance(event)) {
                            wrappedHandler.accept(eventClass.cast(event));
                        }
                    },
                    plugin,
                    ignoreCancelled
            );
        }
    }

    /**
     * Utility method to filter cancelled events
     */
    public static <T extends Cancellable> Predicate<T> notCancelled() {
        return event -> !event.isCancelled();
    }

    /**
     * Utility method to run handler async
     */
    public static <T extends Event> Consumer<T> async(Plugin plugin, Consumer<T> handler) {
        return event -> plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin,
                () -> handler.accept(event)
        );
    }

    /**
     * Utility method to run handler on next tick
     */
    public static <T extends Event> Consumer<T> nextTick(Plugin plugin, Consumer<T> handler) {
        return event -> plugin.getServer().getScheduler().runTask(
                plugin,
                () -> handler.accept(event)
        );
    }
}