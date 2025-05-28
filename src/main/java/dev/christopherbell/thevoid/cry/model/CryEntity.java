//package dev.christopherbell.thevoid.cry.model;
//
//import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@AllArgsConstructor
//@Builder
//@Entity
//@Getter
//@NoArgsConstructor
//@Setter
//@Table(name = "cry", schema = "void_api")
//public class CryEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column
//  private Long id;
//  @Column
//  private String creationDate;
//  @Column
//  private String expirationDate;
//  @Column
//  private boolean isRootCry;
//  @Column
//  private String lastAmplifiedDate;
//  @Column
//  private String text;
//
//  @ManyToOne(fetch = FetchType.LAZY, optional = false)
//  @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
//  private AccountEntity accountEntity;
//
//}