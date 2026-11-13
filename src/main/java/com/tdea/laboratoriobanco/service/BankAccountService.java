package com.tdea.laboratoriobanco.service;

import com.tdea.laboratoriobanco.dto.AccountResult;
import java.util.List;
import org.springframework.data.domain.Sort;
import com.tdea.laboratoriobanco.exception.AccountNotFoundException;
import com.tdea.laboratoriobanco.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read access to account snapshots; balance changes belong to transfers. */
@Service
public class BankAccountService {
    private final BankAccountRepository accounts;

    /**
     * Creates the account service.
     * @param accounts account repository
     */
    public BankAccountService(BankAccountRepository accounts) {
        this.accounts = accounts;
    }

    /**
     * Reads an account snapshot.
     * @param id account identifier
     * @return account data
     * @throws AccountNotFoundException when the account does not exist
     */
    @Transactional(readOnly = true)
    public AccountResult getAccount(Long id) {
        if (id == null || id <= 0) {
            throw new AccountNotFoundException();
        }
        var account = accounts.findById(id).orElseThrow(AccountNotFoundException::new);
        return new AccountResult(account.getId(), account.getAccountNumber(), account.getBalance());
    }

    /**
     * Lists account snapshots in identifier order.
     * @return available accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResult> listAccounts() {
        return accounts.findAll(Sort.by("id")).stream()
                .map(account -> new AccountResult(account.getId(),
                        account.getAccountNumber(), account.getBalance()))
                .toList();
    }
}
