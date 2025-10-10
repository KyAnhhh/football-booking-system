package vn.phankyanh.shopbongda.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.domain.RedisToken;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {

}
