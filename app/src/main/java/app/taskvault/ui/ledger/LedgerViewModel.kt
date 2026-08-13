package app.taskvault.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.LedgerPersonEntity
import app.taskvault.data.local.LedgerTransactionEntity
import app.taskvault.domain.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    val persons: StateFlow<List<LedgerPersonEntity>> = repository.getPersons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<LedgerTransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Grand total: positive = others owe you, negative = you owe others
    val grandTotal: StateFlow<Double> = allTransactions.map { txns ->
        txns.sumOf { if (it.isCredit) it.amount else -it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Selected person for detail screen
    private val _selectedPersonId = MutableStateFlow<String?>(null)

    val selectedPersonTransactions: StateFlow<List<LedgerTransactionEntity>> =
        _selectedPersonId.flatMapLatest { personId ->
            if (personId != null) repository.getTransactionsForPerson(personId)
            else kotlinx.coroutines.flow.flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPerson(personId: String) {
        _selectedPersonId.value = personId
    }

    fun addPerson(name: String) {
        viewModelScope.launch { repository.addPerson(name) }
    }

    fun deletePerson(id: String) {
        viewModelScope.launch { repository.deletePerson(id) }
    }

    fun addTransaction(personId: String, amount: Double, description: String, isCredit: Boolean) {
        viewModelScope.launch { repository.addTransaction(personId, amount, description, isCredit) }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch { repository.deleteTransaction(id) }
    }

    fun getBalanceForPerson(personId: String, transactions: List<LedgerTransactionEntity>): Double {
        return transactions.filter { it.personId == personId }
            .sumOf { if (it.isCredit) it.amount else -it.amount }
    }
}
