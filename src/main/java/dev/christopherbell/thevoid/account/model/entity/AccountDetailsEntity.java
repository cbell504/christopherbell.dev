//package dev.christopherbell.thevoid.account.model.entity;
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
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
//@Table(name = "account_details", schema = "void_api")
//public class AccountDetailsEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column
//  private Long id;
//  @Column
//  private String creationDate;
//  @Column
//  private String firstName;
//  @Column
//  private String lastName;
//  @Column
//  private String lastLoginDate;
//  @Column
//  private String phoneNumber;
//  @OneToOne(cascade = CascadeType.ALL)
//  @JoinColumn(name = "account_id", referencedColumnName = "id")
//  private AccountEntity accountEntity;
//}
