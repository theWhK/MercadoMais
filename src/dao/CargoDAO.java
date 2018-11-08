/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import modelos.Cargo;
import modelos.Setor;

/**
 *
 * @author Laboratorio
 */
public class CargoDAO extends DAO<Cargo>{
    
    @Override
    protected boolean inserir(Cargo element) {
        String sql = "INSERT INTO cargo "
                + "(descricao) values "
                + "(?)";
        try(
            PreparedStatement stmt = conn.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setString(1, element.getDescricao());
            
            System.out.println("sql:"+sql);
            if(stmt.executeUpdate()==1){
                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                int key = keys.getInt(1);
                element.setId(key);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Cargo> listar() {
        List<Cargo> lst = new LinkedList<>();
        lst = org.jdesktop.observablecollections.ObservableCollections.
                                                            observableList(lst);
        
        String sql = "SELECT * FROM cargo;";
        try(
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            while(rs.next()){
                Cargo c = new Cargo();
                c.setId(rs.getInt("id_cargo"));
                c.setDescricao(rs.getString("descricao"));
                
                // Busca os setores permitidos
                String sql2 = "SELECT "
                        + "setor.* "
                        + "FROM "
                        + "setor "
                        + "JOIN cargo_setorespermitidos "
                        + "ON cargo_setorespermitidos.id_setor = setor.id_setor "
                        + "WHERE cargo_setorespermitidos.id_cargo = "+rs.getInt("id_cargo");
                try(
                    PreparedStatement stmt2 = conn.prepareStatement(sql2);
                    ResultSet rs2 = stmt2.executeQuery(sql2);
                ){
                    while(rs2.next()){
                        Setor s = new Setor();
                        s.setId(rs2.getInt("id_setor"));
                        s.setDescricao(rs2.getString("descricao"));
                        c.addSetorPermitido(s);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                
                lst.add(c);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return lst;
    }

    @Override
    protected boolean alterar(Cargo element) {
        String sql = "UPDATE cargo "
                + "SET descricao = ? "
                + "WHERE id_cargo = ?";
        try(
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString(1, element.getDescricao());
            stmt.setInt(2, element.getId());
            
            System.out.println("sql:"+sql);
            if(stmt.executeUpdate()==1){
                return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean gravar(Cargo element) {
        
        if(element.getId()==null){
            return inserir(element);
        }else{
            return alterar(element);
        }
        
    }
    
    public boolean buscar(String busca, List<Cargo> lst){
        lst.clear();
        
        String sql = "SELECT * FROM cargo WHERE descricao LIKE ?;";
        try(
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString(1,"%"+busca+"%");
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                Cargo c = new Cargo();
                c.setId(rs.getInt("id_cargo"));
                c.setDescricao(rs.getString("descricao"));
                lst.add(c);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public Cargo getById(int id){
        Cargo c = null;
        
        String sql = "SELECT * "
                + "FROM cargo "
                + "WHERE id_cargo = "+id;
        try(
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            if(rs.next()){
                c = new Cargo();
                c.setId(rs.getInt("id_cargo"));
                c.setDescricao(rs.getString("descricao"));             
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
        return c;
    }
    
    public boolean apagar(Cargo item) {
        if (item.getId() == null) {
            return false;
        }
        
        String sql = "DELETE FROM cargo WHERE id_cargo = ?";
        try(
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setInt(1,item.getId());
            
            if(stmt.executeUpdate()==1){
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
