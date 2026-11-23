class HttpError extends Error {
  constructor(status, message, errors) {
    super(message);
    this.status = status;
    this.errors = errors; // objek {field: pesan} untuk kesalahan validasi
  }
}
module.exports = HttpError;