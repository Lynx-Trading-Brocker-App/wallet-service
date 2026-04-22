package com.lynx.wallet_service.wallet.repository;

import com.lynx.wallet_service.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserIdAndCurrency(UUID userId, String currency);

    List<Wallet> findAllByUserId(UUID userId);

    boolean existsByUserIdAndCurrency(UUID userId, String currency);
}