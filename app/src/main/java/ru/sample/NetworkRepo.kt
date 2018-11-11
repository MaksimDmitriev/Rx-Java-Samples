package ru.sample

import io.reactivex.Observable

class NetworkRepo : Repo {

    override fun getData(): Observable<Data> {
        return Observable.just(
                Data("network1", 1000L),
                Data("network2", 1001L),
                Data("network3", 1002L)
        )
    }
}