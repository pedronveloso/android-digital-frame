package com.pedronveloso.digitalframe.utils.log

class LogStoreProvider {
    companion object {
        private var logStore: LogStore = InMemoryLogStore()

        fun getLogStore(): LogStore {
            return logStore
        }

        fun setLogStore(newLogStore: LogStore) {
            logStore = newLogStore
        }
    }
}