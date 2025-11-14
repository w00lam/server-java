//////////////////////////////////////////////////////
// User
//////////////////////////////////////////////////////

Table users {
  id long [pk, increment]
  login_id varchar(50) [not null, unique]
  password varchar(255) [not null]
  created_at datetime [default: `now()`]
}

//////////////////////////////////////////////////////
// Queue Token
//////////////////////////////////////////////////////

Table queue_tokens {
  id long [pk, increment]
  user_id long [not null]
  token varchar(100) [not null, unique]
  status varchar(20) [not null] // WAITING, ACTIVE, EXPIRED
  position int
  created_at datetime [default: `now()`]
  expired_at datetime
}

Ref: queue_tokens.user_id > users.id

//////////////////////////////////////////////////////
// Wallet
//////////////////////////////////////////////////////

Table wallets {
  id long [pk, increment]
  user_id long [not null, unique]
  balance int [not null, default: 0]
  updated_at datetime [default: `now()`]
}

Ref: wallets.user_id > users.id

Table wallet_transactions {
  id long [pk, increment]
  wallet_id long [not null]
  amount int [not null]
  type varchar(20) [not null] // CHARGE, PAYMENT
  created_at datetime [default: `now()`]
}

Ref: wallet_transactions.wallet_id > wallets.id

//////////////////////////////////////////////////////
// Concert Schedules
//////////////////////////////////////////////////////

Table schedules {
  id int [pk, increment]
  date date [not null]
  created_at datetime [default: `now()`]
}

//////////////////////////////////////////////////////
// Seats
//////////////////////////////////////////////////////

Table seats {
  id int [pk, increment]
  schedule_id int [not null]
  seat_number int [not null]
  zone_id int [not null]
  status varchar(20) [not null, default: 'AVAILABLE'] // AVAILABLE, HELD, RESERVED
}

Table seat_zones {
  id int [pk, increment]
  seat_id int [not null]
  zone_name varchar(50) [not null]  // VIP, R, S 등
  price int [not null]
}

Ref: seats.schedule_id > schedules.id
Ref: seat_zones.seat_id > seats.id

//////////////////////////////////////////////////////
// Reservations (Temporary Hold)
//////////////////////////////////////////////////////

Table reservations {
  id long [pk, increment]
  user_id int [not null]
  schedule_id int [not null]
  seat_id int [not null]
  queue_token varchar(100) [not null]
  status varchar(20) [not null] // HOLD, CONFIRMED, EXPIRED
  hold_expires_at datetime
  created_at datetime [default: `now()`]
}

Ref: reservations.user_id > users.id
Ref: reservations.schedule_id > schedules.id
Ref: reservations.seat_id > seats.id

//////////////////////////////////////////////////////
// Payment
//////////////////////////////////////////////////////

Table payments {
  id long [pk, increment]
  reservation_id int [not null]
  user_id int [not null]
  amount int [not null]
  status varchar(20) [not null] // SUCCESS, FAIL
  created_at datetime [default: `now()`]
}

Table payment_history {
  id long [pk]
  user_id long
  schedule_id long [null]
  seat_id int [null]
  amount int
  payment_type varchar // charge or use
  status varchar // success/fail/pending
  queue_token varchar
  created_at datetime
  updated_at datetime
}

Ref: payments.reservation_id > reservations.id
Ref: payments.user_id > users.id
Ref: payment_history.user_id > users.id
Ref: payment_history.schedule_id > schedules.id
Ref: payment_history.seat_id > seats.id
