use anyhow::Result;
use nom::{
    character::complete::{line_ending, u32 as nom_u32},
    combinator::map,
    multi::separated_list1,
    sequence::pair,
    IResult,
};

fn parse(input: &str) -> IResult<&str, Vec<u32>> {
    separated_list1(pair(line_ending, line_ending), parse_lines)(input)
}

fn parse_lines(input: &str) -> IResult<&str, u32> {
    map(separated_list1(line_ending, nom_u32), |items| {
        items.iter().sum()
    })(input)
}

fn parse_input(input: &'static str) -> Result<Vec<u32>> {
    let (_, input) = parse(input)?;
    Ok(input)
}

#[allow(unused)]
pub fn part1(filename: &'static str) -> Result<u32> {
    Ok(parse_input(filename)?
        .into_iter()
        .max()
        .expect("cannot take max"))
}

#[allow(unused)]
fn part2(mut filename: &'static str) -> Result<u32> {
    let mut input = parse_input(filename)?;
    input.sort();
    Ok(input.into_iter().rev().take(3).sum())
}

#[cfg(test)]
mod tests {
    use super::*;

    const SAMPLE_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day01_sample.txt");
    const INPUT_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day01.txt");

    #[test]
    fn test_part1() {
        assert_eq!(part1(SAMPLE_TEXT).unwrap(), 24000);
        assert_eq!(part1(INPUT_TEXT).unwrap(), 70509);
    }

    #[test]
    fn test_part2() {
        assert_eq!(part2(SAMPLE_TEXT).unwrap(), 45000);
        assert_eq!(part2(INPUT_TEXT).unwrap(), 208567);
    }
}
