use core::iter::Sum;
use std::fmt::Debug;
use std::fs;
use std::str::FromStr;

fn parse_input<T: Sum + FromStr>(filename: &str) -> Vec<T>
where
    <T as FromStr>::Err: Debug,
{
    let contents = fs::read(filename).expect("couldn't read file {filename}");
    String::from_utf8(contents)
        .expect("contents not valid utf-8")
        .strip_suffix('\n')
        .expect("cannot strip suffix")
        .split("\n\n")
        .collect::<Vec<&str>>()
        .into_iter()
        .map(|x| {
            x.split('\n')
                .map(|x| x.parse::<T>().expect("cannot parse int from str"))
                .sum::<T>()
        })
        .collect::<Vec<T>>()
}

#[allow(unused)]
fn part1(filename: &str) -> i32 {
    parse_input(filename)
        .into_iter()
        .max()
        .expect("cannot take max")
}

#[allow(unused)]
fn part2(filename: &str) -> i32 {
    let mut input: Vec<i32> = parse_input(filename);
    input.sort();
    input.into_iter().rev().take(3).sum()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_part1() {
        let m = part1("/home/steve/Documents/advent-of-code-2022/data/day01_sample.txt");
        assert_eq!(m, 24000);

        let m = part1("/home/steve/Documents/advent-of-code-2022/data/day01.txt");
        assert_eq!(m, 70509);
    }

    #[test]
    fn test_part2() {
        let m = part2("/home/steve/Documents/advent-of-code-2022/data/day01_sample.txt");
        assert_eq!(m, 45000);

        let m = part2("/home/steve/Documents/advent-of-code-2022/data/day01.txt");
        assert_eq!(m, 208567);
    }
}
