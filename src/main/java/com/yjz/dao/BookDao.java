package com.yjz.dao;

import com.yjz.dto.BuyDetailAll;
import com.yjz.entity.Book;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookDao extends BaseDao{

    /**
     * 获得所有图书信息(未加查询条件和翻页参数)----图片封面的pic需要单独读取
     * @return
     * @throws SQLException
     */
    public List<Book> getAllBook() throws SQLException {
        List<Book> bkList = null;
        String sql = "select id,bname,press,author,price,publishdate from tbook";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs != null){
            bkList = new ArrayList<Book>();
            while (rs.next()){
                Book bk = new Book();
                bk.setId(rs.getInt("id"));
                bk.setBname(rs.getString("bname"));
                bk.setPress(rs.getString("press"));
                bk.setAuthor(rs.getString("author"));
                bk.setPrice(rs.getDouble("price"));
                bk.setPublishDate(rs.getDate("publishdate"));
                bkList.add(bk);
            }
        }
        return bkList;
    }

    /**
     * 根据书的id,读取书的封面图片
     * @param bid
     * @return
     * @throws SQLException
     */
    public byte[] getPic(int bid) throws SQLException {
        byte[] pic = null;
        String sql = "select pic from tbook where id=?";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bid);
        ResultSet rs = ps.executeQuery();
        if (rs != null){
            while (rs.next()){
                pic = rs.getBytes("pic");
                break;
            }
        }
        return pic;
    }

    /**
     * 根据书的ID,提取书的详细信息(不含图片)
     * @param bid
     * @return
     * @throws SQLException
     */
    public Book getBookDetail(int bid) throws SQLException {
        Book bk = null;
        String sql = "select id,bname,press,author,price,bindex from tbook where id=?";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bid);
        ResultSet rs = ps.executeQuery();
        if (rs != null){
            while (rs.next()){
                bk = new Book();
                bk.setId(rs.getInt("id"));
                bk.setBname(rs.getString("bname"));
                bk.setPress(rs.getString("press"));
                bk.setAuthor(rs.getString("author"));
                bk.setPrice(rs.getDouble("price"));
                bk.setBindex(rs.getString("bindex"));
                break;
            }
        }
        return bk;
    }

    /**
     * 提取购物车中所有书的信息
     * @param bkCar
     * @return
     * @throws SQLException
     */
    public List<Book> getShopCarBooks(Set<Integer> bkCar) throws SQLException {
        List<Book> books = null;
        String bids = "";
        if (bkCar != null){
            Object[] objs = bkCar.toArray();
            for (int i = 0; i < objs.length; i++){
                if (i == 0){
                    bids = objs[0].toString();
                } else {
                    bids = bids + "," + objs[i].toString();
                }
            }
        }
        String sql = "select id,bname,press,author,price from tbook where id in (" + bids + ")";
        this.openConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs != null){
            books = new ArrayList<Book>();
            while (rs.next()){
                Book bk = new Book();
                bk.setId(rs.getInt("id"));
                bk.setBname(rs.getString("bname"));
                bk.setPress(rs.getString("press"));
                bk.setAuthor(rs.getString("author"));
                bk.setPrice(rs.getDouble("price"));
                books.add(bk);
            }
        }
        return books;
    }

    /**
     * 显示用户的所有购买记录(未加查询条件和翻页参数)----多表联合查询
     * @return
     * @throws SQLException
     */
    public List<BuyDetailAll> getBuyDetail() throws SQLException {
        List<BuyDetailAll> all = null;
        String sql = "select b.bname,b.author,bd.bprice,bd.bcount,br.bdate,u.uname " +
                " from buydetail bd,buyrecord br,tbook b,tuser u " +
                " where br.id=bd.buyid and b.id=bd.bid and u.uname=br.uname order by bdate desc";
        this.openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs != null){
            all = new ArrayList<BuyDetailAll>();
            while (rs.next()){
                BuyDetailAll detail = new BuyDetailAll();
                detail.setBname(rs.getString("bname"));
                detail.setAuthor(rs.getString("author"));
                detail.setBprice(rs.getDouble("bprice"));
                detail.setBdate(rs.getDate("bdate"));
                detail.setBcount(rs.getInt("bcount"));
                detail.setUname(rs.getString("uname"));
                all.add(detail);
            }
        }
        return all;
    }
}
