package com.jwtapp.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtapp.demo.entity.Data;

public interface DataRepository extends JpaRepository<Data, Long> {

	public List<Data> findByStatus(boolean status);

	public Data findByFilename(String filename);
}
