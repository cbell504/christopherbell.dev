/**
 * Tiny publish/subscribe utility (in-memory, per-tab).
 *
 * Usage:
 *   pubsub.subscribe('topic', handler)
 *   pubsub.publish('topic', data)
 */
class PubSub {
    /** Initialize a new pub/sub registry. */
    constructor() {
        this.events = {};
    }

    /**
     * Subscribe a handler to a topic.
     * @param {string} event topic name
     * @param {(data:any)=>void} handler callback for published data
     */
    subscribe(event, handler) {
        if (!this.events[event]) {
            this.events[event] = [];
        }
        this.events[event].push(handler);
    }

    /**
     * Publish data to all handlers subscribed to a topic.
     * @param {string} event topic name
     * @param {any} data payload to pass to handlers
     */
    publish(event, data) {
        if (this.events[event]) {
            this.events[event].forEach((handler) => handler(data));
        }
    }
}

const pubsub = new PubSub();
export default pubsub;
