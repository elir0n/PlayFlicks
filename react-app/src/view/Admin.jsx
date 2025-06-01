import { useState, useEffect } from "react";
import CategoryManager from "../components/admin/CategoryManager";
import MovieManager from "../components/admin/MovieManager";
import TopBar from "../components/admin/TopBar";
import styles from '../styles/admin.module.css';

function Admin() {
  return (
    <>
    <TopBar />
    <div className={styles.contentContainer}>
      <CategoryManager />
      <MovieManager />
    </div>
    </>
  );
}

export default Admin;