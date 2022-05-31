package com.nonamepk.levantalo.domain.use_case

import com.nonamepk.levantalo.domain.repository.ItemRepository

class GetItems(
    private val repository: ItemRepository
) {
    operator fun invoke() = repository.getItemsFromFirestore()
}