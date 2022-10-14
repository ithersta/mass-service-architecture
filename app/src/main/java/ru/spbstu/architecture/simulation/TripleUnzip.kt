package ru.spbstu.architecture.simulation

fun <A, B, C> Iterable<Triple<A, B, C>>.unzip(): Triple<List<A>, List<B>, List<C>> {
    return Triple(ArrayList<A>(), ArrayList<B>(), ArrayList<C>()).apply {
        for (pair in this@unzip) {
            first.add(pair.first)
            second.add(pair.second)
            third.add(pair.third)
        }
    }
}