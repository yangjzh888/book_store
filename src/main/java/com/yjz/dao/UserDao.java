package com.yjz.dao;

import com.yjz.entity.Book;
import com.yjz.entity.BuyDetail;
import com.yjz.entity.BuyRecord;
import com.yjz.entity.User;
import com.yjz.util.Log;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.util.List;

public class UserDao extends BaseDao {

    /**
     * 用户登录
     * @param uname
     * @param pwd
     * @return 返回值中包含权限信息(role)
     * @throws SQLException
     */
    public User login(String uname, String pwd) throws SQLException {
        Log.logger.info(uname + "正在登录");
        User user = null;
        String sql = "select * from tuser where uname = '" + uname + "' and pwd = '" + pwd + "'";
        // 打开数据库
        this.openConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs != null){
            while (rs.next()){
                user = new User();
                String role = rs.getString("role");
                user.setUname(uname);
                user.setPwd(pwd);
                user.setRole(role);
                user.setAccount(rs.getDouble("account"));
                Log.logger.info(uname + "登录成功");
                break;
            }
        }
        return user;
    }

    /**
     * 账户充值,账户扣款
     * @param uname
     * @param money 充值为正数,扣款传负数
     * @throws SQLException
     */
    public void updateUserAccount(String uname, double money) throws SQLException {
        String sql = "update tuser set account=account-? where uname=?";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, money);
        ps.setString(2, uname);
        ps.executeUpdate();
    }

    /**
     * 添加购买记录和购买明细
     * @param br
     * @param books
     * @throws SQLException
     */
    public void addBuyRecord(BuyRecord br, List<Book> books) throws SQLException {
        String sql = "insert into buyrecord(id,uname,bdate,allmoney,allbook)" +
                " values( (select nvl(max(id),0) + 1) from buyrecord),?,?,?,?)";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, br.getUname());
        ps.setDate(2, new Date(br.getBdate().getTime()));
        ps.setDouble(3, br.getAllmoney());
        ps.setInt(4, br.getAllbook());
        ps.executeUpdate();
        for (Book bk : books){
            BuyDetail bd = new BuyDetail();
            // 主键自增长
            // bd.setPid(pid);
            // 外键
            // bd.setBuyid(buyid);
            bd.setBcount(bk.getBuycount());
            bd.setBid(bk.getId());
            bd.setBprice(bk.getPrice());
            addBuyDetail(bd);

        }
    }

    /**
     * 添加购买明细
     * @param bd
     */
    private void addBuyDetail(BuyDetail bd) throws SQLException {
        String sql = "insert into buydetail(pid,buyid,bid,bcount,bprice)" +
                " values( (select nvl(max(pid),0)+1 from buydetail)," +
                " (select max(id) from buyrecord),?,?,?)";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bd.getBid());
        ps.setInt(2,bd.getBcount());
        ps.setDouble(3, bd.getBprice());
        ps.executeUpdate();
        updateBookCount(bd.getBid(), -bd.getBcount());
    }

    /**
     * 修改书的库存数量
     * @param bid
     * @param bcount 补库存为正数,销售为负数
     * @throws SQLException
     */
    private void updateBookCount(int bid, int bcount) throws SQLException {
        String sql = "update tbook set bcount=bcount-? where id=?";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bcount);
        ps.setInt(2, bid);
        ps.executeUpdate();
    }

}
