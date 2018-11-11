package ru.sample

import io.reactivex.Observable

interface Repo {

    fun getData() : Observable<Data>
}