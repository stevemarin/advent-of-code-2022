use anyhow::Result;
use hashbrown::{HashMap, HashSet};
use nom::{
    character::complete::{alpha1, line_ending, newline},
    combinator::map,
    multi::{count, many1, separated_list1},
    sequence::terminated,
    IResult,
};

fn _get_priority() -> HashMap<char, usize> {
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        .chars()
        .into_iter()
        .enumerate()
        .map(|(idx, c)| (c, idx + 1))
        .collect()
}

fn score_duplicated(input: &str) -> usize {
    let priority = _get_priority();
    let input_array: Vec<char> = input.chars().collect();
    let midpoint = input_array.len() / 2;

    let first: HashSet<&char> = input_array[..midpoint].into_iter().collect();
    let second: HashSet<&char> = input_array[midpoint..].into_iter().collect();

    let duplicated = **first
        .intersection(&second)
        .collect::<Vec<&&char>>()
        .first()
        .unwrap();

    let score = priority
        .get(duplicated)
        .expect("cannot find letter in priority")
        .clone();

    score
}

fn parse_part1(input: &str) -> IResult<&str, Vec<usize>> {
    separated_list1(line_ending, parse_lines_part1)(input)
}

fn parse_lines_part1(input: &str) -> IResult<&str, usize> {
    map(alpha1, |x| score_duplicated(x))(input)
}

#[allow(unused)]
fn part1(input: &'static str) -> Result<usize> {
    let (_, input) = parse_part1(input)?;
    Ok(input.iter().sum())
}

fn get_line(input: &str) -> IResult<&str, &str> {
    terminated(alpha1, newline)(input)
}

fn get_3lines(input: &str) -> IResult<&str, Vec<&str>> {
    count(get_line, 3)(input)
}

fn parse_part2(input: &str) -> IResult<&str, Vec<Vec<&str>>> {
    many1(get_3lines)(input)
}

fn score_bag_overlap(bags: Vec<&str>) -> Result<usize> {
    let sets: Vec<HashSet<char>> = bags.into_iter().map(|x| x.chars().collect()).collect();
    let intersect: Vec<&char> = sets[0]
        .iter()
        .filter(|c| sets[1..].iter().all(|s| s.contains(*c)))
        .into_iter()
        .collect();
    assert!(intersect.len() == 1);

    let priority = _get_priority();
    let score = *priority
        .get(*intersect.first().expect("intersect doesn't have first"))
        .expect("value not in priority");

    Ok(score)
}

#[allow(unused)]
fn part2(input: &'static str) -> Result<usize> {
    let (_, input) = parse_part2(input)?;

    let s: usize = input
        .into_iter()
        .map(|x| score_bag_overlap(x).unwrap())
        .sum();

    Ok(s)
}

#[cfg(test)]
mod tests {
    use super::*;

    const SAMPLE_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day03_sample.txt");
    const INPUT_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day03.txt");

    #[test]
    fn test_part1() {
        assert_eq!(part1(SAMPLE_TEXT).unwrap(), 157);
        assert_eq!(part1(INPUT_TEXT).unwrap(), 8515);
    }

    #[test]
    fn test_part2() {
        assert_eq!(part2(SAMPLE_TEXT).unwrap(), 70);
        assert_eq!(part2(INPUT_TEXT).unwrap(), 2434);
    }
}
