package ru.sample

import io.reactivex.Observable

class LocalRepo : Repo {

    override fun getRawData() = Observable.just("local string")

    override fun getData(): Observable<Data> {
        return Observable.just(
                Data("local1", 700L),
                Data("local2", 701L),
                Data("local3", 1004L)
        )
    }
}