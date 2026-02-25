package service.impl;

import dao.TransactionDao;
import dao.UserBalanceDao;
import dao.UserPermissionDao;
import dao.impl.TransactionDaoImpl;
import dao.impl.UserBalanceDaoImpl;
import dao.impl.UserPermissionDaoImpl;
import util.Tool;
import vo.TransactionVO;
import service.EconomyService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EconomyServiceImpl implements EconomyService {

    private final UserBalanceDao balanceDao = new UserBalanceDaoImpl();
    private final TransactionDao txDao = new TransactionDaoImpl();
    private final UserPermissionDao permissionDao = new UserPermissionDaoImpl();

    @Override
    public int topUp(int userId, int amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Top-up amount must be > 0");

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try {
            conn.setAutoCommit(false);

            int oldBalance = balanceDao.getBalance(conn, userId);
            int newBalance = oldBalance + amount;

            balanceDao.updateBalance(conn, userId, newBalance);
            txDao.insert(conn, userId, amount, "TOPUP +" + amount);

            conn.commit();
            return newBalance;

        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception ignore) {}
            throw ex;

        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignore) {}
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public int purchaseCategory(int userId, String category, int price) throws Exception {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be > 0");
        }
        String c = category.trim();

        Connection conn = Tool.getDb();
        if (conn == null) throw new SQLException("DB connection failed.");

        try {
            conn.setAutoCommit(false);

            // 1) 已購買就直接回傳（不要再扣點）
            if (permissionDao.hasCategory(conn, userId, c)) {
                int bal = balanceDao.getBalance(conn, userId);
                conn.commit();
                return bal;
            }

            // 2) 檢查餘額
            int oldBalance = balanceDao.getBalance(conn, userId);
            if (oldBalance < price) {
                throw new Exception("Insufficient balance. Need " + price + " points.");
            }

            int newBalance = oldBalance - price;

            // 3) 扣點
            balanceDao.updateBalance(conn, userId, newBalance);

            // 4) 寫交易（負數代表支出）
            txDao.insert(conn, userId, -price, "PURCHASE " + c + " -" + price);

            // 5) 寫解鎖
            //   你已經加 UNIQUE(user_id, category_purchased)，若同時重複購買會拋例外
            permissionDao.insert(conn, userId, c);

            conn.commit();
            return newBalance;

        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception ignore) {}
            throw ex;

        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignore) {}
            try { conn.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public List<TransactionVO> getTransactionHistoryByUserId(int userId) throws Exception {
        return txDao.findHistoryByUserId(userId);
    }
}