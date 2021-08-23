module.exports = {
  HOST: "localhost",
  USER: "NickoDob",
  PASSWORD: "0812",
  DB: "my_db",
  dialect: "postgres",
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
};