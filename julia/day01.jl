using Chain

function parse_input(filename::String)
    @chain begin
        open("data/$filename", "r") do io
            read(io, String)
        end
        strip(_, '\n')
        split(_, "\n\n")
        map(x -> split(x, "\n"), _)
        map(x -> parse.(Int32, x), _)
        map(sum, _)
    end
end

function part1(filename::String)
    @chain begin
        parse_input(filename)
        reduce(max, _)
    end
end

function part2(filename::String)
    @chain begin
        parse_input(filename)
        sort(_, rev=true)
        getindex(_, 1:3)
        sum
    end
end

@assert(part1("day01_sample.txt") == 24000)
@assert(part1("day01.txt") == 70509)

@assert(part2("day01_sample.txt") == 45000)
@assert(part2("day01.txt") == 208567)
