use anyhow::{anyhow, Error, Result};
use nom::{
    character::complete::{alpha1, line_ending, space1},
    combinator::map,
    multi::separated_list1,
    sequence::separated_pair,
    IResult,
};

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
enum Toss {
    Rock,
    Paper,
    Scissors,
}

impl From<Toss> for u32 {
    fn from(value: Toss) -> Self {
        match value {
            Toss::Rock => 1,
            Toss::Paper => 2,
            Toss::Scissors => 3,
        }
    }
}

impl TryFrom<&str> for Toss {
    type Error = Error;

    fn try_from(input: &str) -> Result<Toss> {
        match input {
            "X" | "A" => Ok(Toss::Rock),
            "Y" | "B" => Ok(Toss::Paper),
            "Z" | "C" => Ok(Toss::Scissors),
            x => Err(anyhow!("invalid input character: {:?} for Toss", x)),
        }
    }
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
enum Outcome {
    Lose,
    Draw,
    Win,
}

impl From<Outcome> for u32 {
    fn from(value: Outcome) -> Self {
        match value {
            Outcome::Lose => 0,
            Outcome::Draw => 3,
            Outcome::Win => 6,
        }
    }
}

impl TryFrom<&str> for Outcome {
    type Error = Error;

    fn try_from(input: &str) -> Result<Outcome> {
        match input {
            "X" => Ok(Outcome::Lose),
            "Y" => Ok(Outcome::Draw),
            "Z" => Ok(Outcome::Win),
            x => Err(anyhow!("invalid input character: {:?} for Outcome", x)),
        }
    }
}

#[allow(unused)]
struct Game {
    oppo: Toss,
    me: Toss,
    outcome: Outcome,
}

impl From<Game> for u32 {
    fn from(game: Game) -> Self {
        let toss_score: u32 = game.me.into();
        let outcome_score: u32 = game.outcome.into();
        toss_score + outcome_score
    }
}

impl Game {
    fn from_tosses(oppo: Toss, me: Toss) -> Game {
        let outcome = match (oppo, me) {
            (Toss::Rock, Toss::Rock) => Outcome::Draw,
            (Toss::Rock, Toss::Paper) => Outcome::Win,
            (Toss::Rock, Toss::Scissors) => Outcome::Lose,
            (Toss::Paper, Toss::Rock) => Outcome::Lose,
            (Toss::Paper, Toss::Paper) => Outcome::Draw,
            (Toss::Paper, Toss::Scissors) => Outcome::Win,
            (Toss::Scissors, Toss::Rock) => Outcome::Win,
            (Toss::Scissors, Toss::Paper) => Outcome::Lose,
            (Toss::Scissors, Toss::Scissors) => Outcome::Draw,
        };

        Game { oppo, me, outcome }
    }

    fn from_outcome(oppo: Toss, outcome: Outcome) -> Game {
        let me = match (oppo, outcome) {
            (Toss::Rock, Outcome::Win) => Toss::Paper,
            (Toss::Rock, Outcome::Lose) => Toss::Scissors,
            (Toss::Rock, Outcome::Draw) => Toss::Rock,
            (Toss::Paper, Outcome::Win) => Toss::Scissors,
            (Toss::Paper, Outcome::Lose) => Toss::Rock,
            (Toss::Paper, Outcome::Draw) => Toss::Paper,
            (Toss::Scissors, Outcome::Win) => Toss::Rock,
            (Toss::Scissors, Outcome::Lose) => Toss::Paper,
            (Toss::Scissors, Outcome::Draw) => Toss::Scissors,
        };

        Game { oppo, me, outcome }
    }
}

fn parse_part1(input: &str) -> IResult<&str, Vec<Game>> {
    separated_list1(line_ending, parse_lines_part1)(input)
}

fn parse_lines_part1(input: &str) -> IResult<&str, Game> {
    map(
        separated_pair(alpha1, space1, alpha1),
        |(o, m): (&str, &str)| {
            let oppo = Toss::try_from(o).unwrap();
            let me = Toss::try_from(m).unwrap();
            Game::from_tosses(oppo, me)
        },
    )(input)
}

fn parse_part2(input: &str) -> IResult<&str, Vec<Game>> {
    separated_list1(line_ending, parse_lines_part2)(input)
}

fn parse_lines_part2(input: &str) -> IResult<&str, Game> {
    map(
        separated_pair(alpha1, space1, alpha1),
        |(oppo, outcome): (&str, &str)| {
            let oppo = Toss::try_from(oppo).unwrap();
            let outcome = Outcome::try_from(outcome).unwrap();
            Game::from_outcome(oppo, outcome)
        },
    )(input)
}

#[allow(unused)]
fn part1(input: &'static str) -> Result<u32> {
    let (_, input) = parse_part1(input)?;
    let score: u32 = input.into_iter().map(|x| u32::from(x)).sum();
    Ok(score)
}

#[allow(unused)]
fn part2(input: &'static str) -> Result<u32> {
    let (_, input) = parse_part2(input)?;
    let score: u32 = input.into_iter().map(|x| u32::from(x)).sum();
    Ok(score)
}

#[cfg(test)]
mod tests {
    use super::*;

    const SAMPLE_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day02_sample.txt");
    const INPUT_TEXT: &str =
        include_str!("/home/steve/Documents/advent-of-code-2022/data/day02.txt");

    #[test]
    fn test_part1() {
        assert_eq!(part1(SAMPLE_TEXT).unwrap(), 15);
        assert_eq!(part1(INPUT_TEXT).unwrap(), 12586);
    }

    #[test]
    fn test_part2() {
        assert_eq!(part2(SAMPLE_TEXT).unwrap(), 12);
        assert_eq!(part2(INPUT_TEXT).unwrap(), 13193);
    }
}
