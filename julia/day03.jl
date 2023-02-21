using Chain

priority = merge(Dict(zip('a':'z', 1:26)), Dict(zip('A':'Z', 27:52)))

function split_in_half(bag)
    l = length(bag)
    midpoint = l ÷ 2
    chars = map(only, bag)

    first_half = Set(chars[1:midpoint])
    second_half = Set(chars[midpoint+1:end])

    intersection = collect(intersect(first_half, second_half))
    @assert(length(intersection) == 1)

    first(intersection)
end

function parse_input(filename::String)
    @chain begin
        open("data/$filename", "r") do io
            read(io, String)
        end
        strip(_, '\n')
        split(_, '\n')
        map(x -> split(x, ""), _)
    end

end

function part1(filename)
    @chain begin
        parse_input(filename)
        map(split_in_half, _)
        map(x -> priority[x], _)
        sum(_)
    end
end

function part2(filename)
    @chain begin
        parse_input(filename)
        collect(Iterators.partition(_, 3))
        map(x -> reduce(intersect, x), _)
        map(first, _)
        map(x -> priority[only(x)], _)
        sum(_)
    end
end

@assert(part1("day03_sample.txt") == 157)
@assert(part1("day03.txt") == 8515)

@assert(part2("day03_sample.txt") == 70)
@assert(part2("day03.txt") == 2434)
