package com.junjie.share.serviceimpl

import com.junjie.core.exception.NotFoundException
import com.junjie.share.database.account.dao.UserInfoDao
import com.junjie.share.database.account.entity.UserInfo
import com.junjie.share.service.UserInfoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class UserInfoServiceImpl(private val userInfoDao: UserInfoDao) : UserInfoService {
    //保存
    override fun save(info: UserInfo): UserInfo {
        return userInfoDao.save(info)
    }

    //获取用户信息
    override fun get(id: Int): UserInfo {
        return userInfoDao.findById(id).orElseThrow { NotFoundException("用户信息不存在") }
    }

    //根据账户id获取用户信息
    override fun getByUserId(userId: Int): UserInfo {
        return userInfoDao.findOneByUserId(userId).orElseThrow { NotFoundException("用户信息不存在") }
    }

    //获取全部用户
    override fun list(name: String?): List<UserInfo> {
        return if (name != null && name.isNotEmpty()) {
            userInfoDao.findAllByNicknameLike("%$name%")
        } else {
            userInfoDao.findAll()
        }
    }

    //获取分页
    override fun paging(pageable: Pageable, name: String?, accountIdList: List<String>): Page<UserInfo> {
        val specification = Specification<UserInfo> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (accountIdList.isNotEmpty()) {
                val path = root.get<String>("accountId")
                val inValue = criteriaBuilder.`in`(path)
                accountIdList.forEach {
                    inValue.value(it)
                }
                predicatesList.add(criteriaBuilder.and(inValue))
            }
            if (name != null && name.isNotEmpty()) {
                predicatesList.add(criteriaBuilder.like(root.get<String>("name"), "%$name%"))
            }
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
        return userInfoDao.findAll(specification, pageable)
    }
}