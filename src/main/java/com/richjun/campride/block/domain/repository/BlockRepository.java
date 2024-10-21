package com.richjun.campride.block.domain.repository;

import com.richjun.campride.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
