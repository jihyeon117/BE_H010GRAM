package com.hanghae.be_h010gram.domain.post.repository;

import com.hanghae.be_h010gram.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}