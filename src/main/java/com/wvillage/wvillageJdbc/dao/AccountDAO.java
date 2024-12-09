package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AccountDAO {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ACCOUNT ="SELECT ACC_NUM, ACC_BACK FROM ACCOUNT WHERE ACC_EMAIL = ?";
    private static final String INSERT_ACCOUNT ="INSERT INTO ACCOUNT (ACC_NUM, ACC_BACK, ACC_EMAIL) VALUES (?, ?, ?)";
    private static final String DELETE_ACCOUNT ="DELETE FROM ACCOUNT WHERE ACC_NUM = ? AND ACC_BACK = ?";

    //계좌 조회
    public List<AccountVO> findAccountsByEmail(String ACC_EMAIL) {
        try {
            return jdbcTemplate.query(FIND_ACCOUNT,
                    new Object[]{ACC_EMAIL},
                    (rs, rowNum) -> {
                        AccountVO account = new AccountVO();
                        account.setAccountNo(rs.getString("ACC_NUM"));
                        account.setAccountBank(rs.getString("ACC_BACK"));
                        return account;
                    });
        } catch (DataAccessException e) {
            log.error("이메일로 계좌 조회 중 오류 발생: {}", ACC_EMAIL, e);
            return null;
        }
    }

    //계좌 추가
    public boolean addAccount(AccountVO accountVO) {
        try {
            if (accountVO.getAccountNo() == null || accountVO.getAccountNo().isEmpty()) {
                log.error("계좌 번호가 비어 있습니다: {}", accountVO);
                return false; // 계좌번호가 없으면 삽입하지 않음
            }

            log.info("Attempting to insert account into DB: {}", accountVO); // DB에 삽입 시도 로그
            int result = jdbcTemplate.update(
                    INSERT_ACCOUNT,
                    accountVO.getAccountNo(),
                    accountVO.getAccountBank(),
                    accountVO.getAccountEmail()
            );
            log.info("DB insert result: {}", result); // DB 삽입 결과 확인
            return result > 0;
        } catch (DataAccessException e) {
            log.error("계좌 추가 등록 중 오류 발생: {}", accountVO, e);
            return false;
        }
    }

    //계좌 삭제
    public boolean deleteAccount(int accountNo, String accountBank) {
        try {
            int result = jdbcTemplate.update(
                    DELETE_ACCOUNT,
                    accountNo,
                    accountBank
            );
            return result > 0;
        } catch (DataAccessException e) {
            log.error("계좌 삭제 중 오류 발생: accountNo={}, accountBank={}", accountNo, accountBank, e);
            return false;
        }
    }
}
