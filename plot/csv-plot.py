#!/usr/bin/env python3
"""Convert CSV benchmark data to Gnuplot data files."""

import csv, sqlite3, sys

def main():
  try:
    dbname = sys.argv[1]
  except IndexError:
    dbname = ':memory:'

  db = sqlite3.Connection(dbname)
  c = db.cursor()
  c.execute(
    'create table if not exists results (array_type, method, array_size, time)'
  )
  array_types = ['sorted', 'random', 'almost_sorted', 'reverse']

  for line in csv.DictReader(sys.stdin):
    method = line['method']
    size = line['size']
    for array_type in array_types:
      time = line[f'result_{array_type}']
      c.execute('insert into results values (?, ?, ?, ?)',
        (array_type, method, size, time))
  c.execute('commit')

  c.execute('select distinct method from results')
  methods = [x for x, in c.fetchall()]

  for array_type in array_types:
    with open(f'{array_type}.data', 'w') as f:
      for method in methods:
        method_name = method.replace('_', ' ')
        f.write(f'"{method_name}"\n')
        c.execute('select array_size, time from results where '
          'array_type = ? and method = ? order by cast(array_size as integer)',
          (array_type, method))
        for size, time in c.fetchall():
          f.write(f'{size} {time}\n')
        f.write('\n\n')

if __name__ == '__main__':
  main()
