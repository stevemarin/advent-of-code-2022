using Chain

score_part1 = Dict((
    ((:rock, :rock), 1 + 3),
    ((:rock, :paper), 2 + 6),
    ((:rock, :scissors), 3 + 0),
    ((:paper, :rock), 1 + 0),
    ((:paper, :paper), 2 + 3),
    ((:paper, :scissors), 3 + 6),
    ((:scissors, :rock), 1 + 6),
    ((:scissors, :paper), 2 + 0),
    ((:scissors, :scissors), 3 + 3)
))

score_part2 = Dict((
    ((:rock, :lose), 3 + 0),
    ((:rock, :draw), 1 + 3),
    ((:rock, :win), 2 + 6),
    ((:paper, :lose), 1 + 0),
    ((:paper, :draw), 2 + 3),
    ((:paper, :win), 3 + 6),
    ((:scissors, :lose), 2 + 0),
    ((:scissors, :draw), 3 + 3),
    ((:scissors, :win), 1 + 6)
))

function transform_round_part1(round::Vector)
    rps = Dict([("A", :rock), ("B", :paper), ("C", :scissors), ("X", :rock), ("Y", :paper), ("Z", :scissors)])
    map(x -> rps[x], round)
end

function transform_round_part2(round::Vector)
    rps = Dict([("A", :rock), ("B", :paper), ("C", :scissors), ("X", :lose), ("Y", :draw), ("Z", :win)])
    map(x -> rps[x], round)
end

function parse_input(filename::String)
    @chain begin
        open("data/$filename", "r") do io
            read(io, String)
        end
        strip(_, '\n')
        split(_, '\n')
        map(split, _)
    end
end

function part1(filename::String)
    @chain begin
        parse_input(filename)
        map(transform_round_part1, _)
        map(x -> tuple(x...), _)
        map(x -> score_part1[x], _)
        sum(_)
    end
end

function part2(filename::String)
    @chain begin
        parse_input(filename)
        map(transform_round_part2, _)
        map(x -> tuple(x...), _)
        map(x -> score_part2[x], _)
        sum(_)
    end
end

@assert(part1("day02_sample.txt") == 15)
@assert(part1("day02.txt") == 12586)

@assert(part2("day02_sample.txt") == 12)
@assert(part2("day02.txt") == 13193)
