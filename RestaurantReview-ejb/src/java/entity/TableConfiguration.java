/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;


@Entity
public class TableConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableConfigurationId;
    
    @NotNull
    @Column(nullable = false)
    private Integer numOfSmallTable = 0;
    
    @NotNull
    @Column(nullable = false)
    private Integer numOfMediumTable = 0;
    
    @NotNull
    @Column(nullable = false)
    private Integer numOfLargeTable = 0;


    public TableConfiguration() {
    }

    public TableConfiguration(Integer numOfSmallTable, Integer numOfMediumTable, Integer numOfLargeTable) {
        this.numOfSmallTable = numOfSmallTable;
        this.numOfMediumTable = numOfMediumTable;
        this.numOfLargeTable = numOfLargeTable;
    }
    
    
    
    public Long getTableConfigurationId() {
        return tableConfigurationId;
    }

    public void setTableConfigurationId(Long tableConfigurationId) {
        this.tableConfigurationId = tableConfigurationId;
    }

    public Integer getNumOfSmallTable() {
        return numOfSmallTable;
    }

    public void setNumOfSmallTable(Integer numOfSmallTable) {
        this.numOfSmallTable = numOfSmallTable;
    }

    public Integer getNumOfMediumTable() {
        return numOfMediumTable;
    }

    public void setNumOfMediumTable(Integer numOfMediumTable) {
        this.numOfMediumTable = numOfMediumTable;
    }

    public Integer getNumOfLargeTable() {
        return numOfLargeTable;
    }

    public void setNumOfLargeTable(Integer numOfLargeTable) {
        this.numOfLargeTable = numOfLargeTable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tableConfigurationId != null ? tableConfigurationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the tableConfigurationId fields are not set
        if (!(object instanceof TableConfiguration)) {
            return false;
        }
        TableConfiguration other = (TableConfiguration) object;
        if ((this.tableConfigurationId == null && other.tableConfigurationId != null) || (this.tableConfigurationId != null && !this.tableConfigurationId.equals(other.tableConfigurationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TableConfiguration[ id=" + tableConfigurationId + " ]";
    }
    
}
