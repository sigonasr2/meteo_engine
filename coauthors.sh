npm i -g git-mob
cat <<-EOF > ~/.git-coauthors
{
  "coauthors": {
    "sig": {
      "name": "sigonasr2",
      "email": "sigonasr2@gmail.com"
    }
  }
}
EOF
git mob sig 