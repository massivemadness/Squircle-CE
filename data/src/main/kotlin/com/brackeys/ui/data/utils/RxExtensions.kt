/*
 * Copyright 2020 Brackeys IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brackeys.ui.data.utils

import com.brackeys.ui.domain.providers.rx.SchedulersProvider
import io.reactivex.*

// region: Single

fun <T> Single<T>.schedulersIoToMain(schedulersProvider: SchedulersProvider): Single<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.mainThread())
}

fun <T> Single<T>.schedulersIoToComputation(schedulersProvider: SchedulersProvider): Single<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.computation())
}

// endregion: Single

// region: Maybe

fun <T> Maybe<T>.schedulersIoToMain(schedulersProvider: SchedulersProvider): Maybe<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.mainThread())
}

fun <T> Maybe<T>.schedulersIoToComputation(schedulersProvider: SchedulersProvider): Maybe<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.computation())
}

// endregion: Maybe

// region: Observable

fun <T> Observable<T>.schedulersIoToMain(schedulersProvider: SchedulersProvider): Observable<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.mainThread())
}

fun <T> Observable<T>.schedulersIoToComputation(schedulersProvider: SchedulersProvider): Observable<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.computation())
}

// endregion: Observable

// region: Flowable

fun <T> Flowable<T>.schedulersIoToMain(schedulersProvider: SchedulersProvider): Flowable<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.mainThread())
}

fun <T> Flowable<T>.schedulersIoToComputation(schedulersProvider: SchedulersProvider): Flowable<T> {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.computation())
}

// endregion: Flowable

// region: Completable

fun Completable.schedulersIoToMain(schedulersProvider: SchedulersProvider): Completable {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.mainThread())
}

fun Completable.schedulersIoToComputation(schedulersProvider: SchedulersProvider): Completable {
    return subscribeOn(schedulersProvider.io()).observeOn(schedulersProvider.computation())
}

// endregion: Completable