package com.pedronveloso.digitalframe.utils.log

class LogStoreProvider {
    companion object {
        private var logStore: LogStore = InMemoryLogStore()

        fun getLogStore(): LogStore = logStore

        fun setLogStore(newLogStore: LogStore) {
            logStore = newLogStore
        }
    }
}
