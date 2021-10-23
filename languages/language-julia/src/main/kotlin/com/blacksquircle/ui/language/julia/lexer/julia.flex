package com.blacksquircle.ui.language.julia.lexer;

@SuppressWarnings("all")
%%

%public
%class JuliaLexer
%type JuliaToken
%function advance
%unicode
%line
%column
%char

%{
  public final int getTokenStart() {
      return (int) yychar;
  }

  public final int getTokenEnd() {
      return getTokenStart() + yylength();
  }
%}

IDENTIFIER = [:jletter:] [:jletterdigit:]*

DIGIT = [0-9]
DIGIT_OR_UNDERSCORE = [_0-9]
DIGITS = {DIGIT} | {DIGIT} {DIGIT_OR_UNDERSCORE}*
HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]

SYMB_OP = [-+*/\\=\^:<>~?&$%|!]
SYMB_LANG = [\(\){}\[\]:'\"`@#]
OPERATOR = {SYMB_OP}| {SYMB_LANG} | \+=|-=|\*=|\/=|\/\/=|\\\\=|\^=|÷=|%=|<<=|>>=|>>>=|\|=|&=|:=|=>|\$=|\|\||&&|<:|>:|\|>|<\||\/\/|\+\+|<=|>=|->|===|==|\!==|\!=

BASE_TYPES = AbstractArray|AbstractChannel|AbstractChar|AbstractDict|AbstractDisplay|AbstractFloat|AbstractIrrational|AbstractMatrix|AbstractRange|AbstractSet|AbstractString|AbstractUnitRange|AbstractVecOrMat|AbstractVector|Any|ArgumentError|Array|AssertionError|BigFloat|BigInt|BitArray|BitMatrix|BitSet|BitVector|Bool|BoundsError|CapturedException|CartesianIndex|CartesianIndices|Cchar|Cdouble|Cfloat|Channel|Char|Cint|Cintmax_t|Clong|Clonglong|Cmd|Colon|Complex|ComplexF16|ComplexF32|ComplexF64|CompositeException|Condition|Cptrdiff_t|Cshort|Csize_t|Cssize_t|Cstring|Cuchar|Cuint|Cuintmax_t|Culong|Culonglong|Cushort|Cvoid|Cwchar_t|Cwstring|DataType|DenseArray|DenseMatrix|DenseVecOrMat|DenseVector|Dict|DimensionMismatch|Dims|DivideError|DomainError|EOFError|Enum|ErrorException|Exception|ExponentialBackOff|Expr|Float16|Float32|Float64|Function|GlobalRef|HTML|IO|IOBuffer|IOContext|IOStream|IdDict|IndexCartesian|IndexLinear|IndexStyle|InexactError|InitError|Int|Int128|Int16|Int32|Int64|Int8|Integer|InterruptException|InvalidStateException|Irrational|KeyError|LinRange|LineNumberNode|LinearIndices|LoadError|MIME|Matrix|Method|MethodError|Missing|MissingException|Module|NTuple|NamedTuple|Nothing|Number|OrdinalRange|OutOfMemoryError|OverflowError|Pair|PartialQuickSort|PermutedDimsArray|Pipe|ProcessFailedException|Ptr|QuoteNode|Rational|RawFD|ReadOnlyMemoryError|Real|ReentrantLock|Ref|Regex|RegexMatch|RoundingMode|SegmentationFault|Set|Signed|Some|StackOverflowError|StepRange|StepRangeLen|StridedArray|StridedMatrix|StridedVecOrMat|StridedVector|String|StringIndexError|SubArray|SubString|SubstitutionString|Symbol|SystemError|Task|Text|TextDisplay|Timer|Tuple|Type|TypeError|TypeVar|UInt|UInt128|UInt16|UInt32|UInt64|UInt8|UndefInitializer|UndefKeywordError|UndefRefError|UndefVarError|Union|UnionAll|UnitRange|Unsigned|Val|Vararg|VecElement|VecOrMat|Vector|VersionNumber|WeakKeyDict|WeakRef
/*BASE_FUNCS = abs|abs2|abspath|accumulate|accumulate\!|acos|acosd|acosh|acot|acotd|acoth|acsc|acscd|acsch|adjoint|all|all\!|allunique|angle|any|any\!|append\!|argmax|argmin|ascii|asec|asecd|asech|asin|asind|asinh|asyncmap|asyncmap\!|atan|atand|atanh|atexit|atreplinit|axes|backtrace|basename|big|bind|binomial|bitstring|broadcast|broadcast\!|bswap|bytes2hex|bytesavailable|cat|catch_backtrace|cbrt|cd|ceil|cglobal|checkbounds|checkindex|chmod|chomp|chop|chown|circcopy\!|circshift|circshift\!|cis|clamp|clamp\!|cld|close|cmp|coalesce|code_lowered|code_typed|codepoint|codeunit|codeunits|collect|complex|conj|conj\!|convert|copy|copy\!|copysign|copyto\!|cos|cosc|cosd|cosh|cospi|cot|cotd|coth|count|count_ones|count_zeros|countlines|cp|csc|cscd|csch|ctime|cumprod|cumprod\!|cumsum|cumsum\!|current_task|deepcopy|deg2rad|delete\!|deleteat\!|denominator|detach|devnull|diff|digits|digits\!|dirname|disable_sigint|display|displayable|displaysize|div|divrem|download|dropdims|dump|eachcol|eachindex|eachline|eachmatch|eachrow|eachslice|eltype|empty|empty\!|endswith|enumerate|eof|eps|error|esc|escape_string|evalfile|exit|exp|exp10|exp2|expanduser|expm1|exponent|extrema|factorial|falses|fd|fdio|fetch|fieldcount|fieldname|fieldnames|fieldoffset|fieldtypes|filemode|filesize|fill|fill\!|filter|filter\!|finalize|finalizer|findall|findfirst|findlast|findmax|findmax\!|findmin|findmin\!|findnext|findprev|first|firstindex|fld|fld1|fldmod|fldmod1|flipsign|float|floatmax|floatmin|floor|flush|fma|foldl|foldr|foreach|frexp|fullname|functionloc|gcd|gcdx|gensym|get|get\!|get_zero_subnormals|gethostname|getindex|getkey|getpid|getproperty|gperm|hasfield|hash|haskey|hasmethod|hasproperty|hcat|hex2bytes|hex2bytes\!|homedir|htol|hton|hvcat|hypot|identity|ifelse|ignorestatus|im|imag|in|include_dependency|include_string|indexin|insert\!|instances|intersect|intersect\!|inv|invmod|invperm|invpermute\!|isabspath|isabstracttype|isapprox|isascii|isassigned|isbits|isbitstype|isblockdev|ischardev|iscntrl|isconcretetype|isconst|isdigit|isdir|isdirpath|isdispatchtuple|isempty|isequal|iseven|isfifo|isfile|isfinite|isimmutable|isinf|isinteger|isinteractive|isless|isletter|islink|islocked|islowercase|ismarked|ismissing|ismount|isnan|isnothing|isnumeric|isodd|isone|isopen|ispath|isperm|ispow2|isprimitivetype|isprint|ispunct|isqrt|isreadable|isreadonly|isready|isreal|issetequal|issetgid|issetuid|issocket|issorted|isspace|issticky|isstructtype|issubnormal|issubset|istaskdone|istaskstarted|istextmime|isuppercase|isvalid|iswritable|isxdigit|iszero|iterate|join|joinpath|keys|keytype|kill|kron|last|lastindex|lcm|ldexp|leading_ones|leading_zeros|length|lock|log|log10|log1p|log2|lowercase|lowercasefirst|lpad|lstat|lstrip|ltoh|macroexpand|map|map\!|mapfoldl|mapfoldr|mapreduce|mapslices|mark|match|max|maximum|maximum\!|maxintfloat|merge|merge\!|methods|min|minimum|minimum\!|minmax|missing|mkdir|mkpath|mktemp|mktempdir|mod|mod1|mod2pi|modf|mtime|muladd|mv|nameof|names|ncodeunits|ndigits|ndims|nextfloat|nextind|nextpow|nextprod|normpath|notify|ntoh|ntuple|numerator|objectid|occursin|oftype|one|ones|oneunit|open|operm|pairs|parent|parentindices|parentmodule|parse|partialsort|partialsort\!|partialsortperm|partialsortperm\!|pathof|permute\!|permutedims|permutedims\!|pi|pipeline|pointer|pointer_from_objref|pop\!|popdisplay|popfirst\!|position|powermod|precision|precompile|prepend\!|prevfloat|prevind|prevpow|print|println|printstyled|process_exited|process_running|prod|prod\!|promote|promote_rule|promote_shape|promote_type|propertynames|push\!|pushdisplay|pushfirst\!|put\!|pwd|rad2deg|rand|randn|range|rationalize|read|read\!|readavailable|readbytes\!|readchomp|readdir|readline|readlines|readlink|readuntil|real|realpath|redirect_stderr|redirect_stdin|redirect_stdout|redisplay|reduce|reenable_sigint|reim|reinterpret|relpath|rem|rem2pi|repeat|replace|replace\!|repr|reset|reshape|resize\!|rethrow|retry|reverse|reverse\!|reverseind|rm|rot180|rotl90|rotr90|round|rounding|rpad|rsplit|rstrip|run|schedule|searchsorted|searchsortedfirst|searchsortedlast|sec|secd|sech|seek|seekend|seekstart|selectdim|set_zero_subnormals|setdiff|setdiff\!|setenv|setindex\!|setprecision|setproperty\!|setrounding|show|showable|showerror|sign|signbit|signed|significand|similar|sin|sinc|sincos|sind|sinh|sinpi|size|sizehint\!|sizeof|skip|skipchars|skipmissing|sleep|something|sort|sort\!|sortperm|sortperm\!|sortslices|splice\!|split|splitdir|splitdrive|splitext|splitpath|sprint|sqrt|stacktrace|startswith|stat|stderr|stdin|stdout|step|stride|strides|string|strip|success|sum|sum\!|summary|supertype|symdiff|symdiff\!|symlink|systemerror|take\!|tan|tand|tanh|task_local_storage|tempdir|tempname|textwidth|thisind|time|time_ns|timedwait|titlecase|to_indices|touch|trailing_ones|trailing_zeros|transcode|transpose|trues|trunc|truncate|trylock|tryparse|typeintersect|typejoin|typemax|typemin|unescape_string|union|union\!|unique|unique\!|unlock|unmark|unsafe_copyto\!|unsafe_load|unsafe_pointer_to_objref|unsafe_read|unsafe_store\!|unsafe_string|unsafe_trunc|unsafe_wrap|unsafe_write|unsigned|uperm|uppercase|uppercasefirst|valtype|values|vcat|vec|view|wait|walkdir|which|widemul|widen|withenv|write|xor|yield|yieldto|zero|zeros|zip|applicable|eval|fieldtype|getfield|ifelse|invoke|isa|isdefined|nfields|nothing|setfield\!|throw|tuple|typeassert|typeof|undef|include
BASE_MACROS = __DIR__|__FILE__|__LINE__|__MODULE__|__dot__|allocated|assert|async|boundscheck|cfunction|cmd|debug|deprecate|doc|elapsed|enum|error|eval|evalpoly|fastmath|generated|gensym|goto|inbounds|info|inline|isdefined|label|macroexpand|macroexpand1|noinline|nospecialize|polly|show|simd|specialize|static|sync|task|threadcall|time|timed|timev|view|views|warn
BASE_MODULES = Base|Broadcast|Docs|GC|Iterators|Libc|MathConstants|Meta|StackTraces|Sys|Threads|Core|Main
BASE_MODULE_FUNCS = Base\.(abs|abs2|abspath|accumulate|accumulate\!|acos|acosd|acosh|acot|acotd|acoth|acsc|acscd|acsch|adjoint|all|all\!|allunique|angle|any|any\!|append\!|argmax|argmin|ascii|asec|asecd|asech|asin|asind|asinh|asyncmap|asyncmap\!|atan|atand|atanh|atexit|atreplinit|axes|backtrace|basename|big|bind|binomial|bitstring|broadcast|broadcast\!|bswap|bytes2hex|bytesavailable|cat|catch_backtrace|cbrt|cd|ceil|cglobal|checkbounds|checkindex|chmod|chomp|chop|chown|circcopy\!|circshift|circshift\!|cis|clamp|clamp\!|cld|close|cmp|coalesce|code_lowered|code_typed|codepoint|codeunit|codeunits|collect|complex|conj|conj\!|convert|copy|copy\!|copysign|copyto\!|cos|cosc|cosd|cosh|cospi|cot|cotd|coth|count|count_ones|count_zeros|countlines|cp|csc|cscd|csch|ctime|cumprod|cumprod\!|cumsum|cumsum\!|current_task|deepcopy|deg2rad|delete\!|deleteat\!|denominator|detach|devnull|diff|digits|digits\!|dirname|disable_sigint|display|displayable|displaysize|div|divrem|download|dropdims|dump|eachcol|eachindex|eachline|eachmatch|eachrow|eachslice|eltype|empty|empty\!|endswith|enumerate|eof|eps|error|esc|escape_string|evalfile|exit|exp|exp10|exp2|expanduser|expm1|exponent|extrema|factorial|falses|fd|fdio|fetch|fieldcount|fieldname|fieldnames|fieldoffset|fieldtypes|filemode|filesize|fill|fill\!|filter|filter\!|finalize|finalizer|findall|findfirst|findlast|findmax|findmax\!|findmin|findmin\!|findnext|findprev|first|firstindex|fld|fld1|fldmod|fldmod1|flipsign|float|floatmax|floatmin|floor|flush|fma|foldl|foldr|foreach|frexp|fullname|functionloc|gcd|gcdx|gensym|get|get\!|get_zero_subnormals|gethostname|getindex|getkey|getpid|getproperty|gperm|hasfield|hash|haskey|hasmethod|hasproperty|hcat|hex2bytes|hex2bytes\!|homedir|htol|hton|hvcat|hypot|identity|ifelse|ignorestatus|im|imag|in|include_dependency|include_string|indexin|insert\!|instances|intersect|intersect\!|inv|invmod|invperm|invpermute\!|isabspath|isabstracttype|isapprox|isascii|isassigned|isbits|isbitstype|isblockdev|ischardev|iscntrl|isconcretetype|isconst|isdigit|isdir|isdirpath|isdispatchtuple|isempty|isequal|iseven|isfifo|isfile|isfinite|isimmutable|isinf|isinteger|isinteractive|isless|isletter|islink|islocked|islowercase|ismarked|ismissing|ismount|isnan|isnothing|isnumeric|isodd|isone|isopen|ispath|isperm|ispow2|isprimitivetype|isprint|ispunct|isqrt|isreadable|isreadonly|isready|isreal|issetequal|issetgid|issetuid|issocket|issorted|isspace|issticky|isstructtype|issubnormal|issubset|istaskdone|istaskstarted|istextmime|isuppercase|isvalid|iswritable|isxdigit|iszero|iterate|join|joinpath|keys|keytype|kill|kron|last|lastindex|lcm|ldexp|leading_ones|leading_zeros|length|lock|log|log10|log1p|log2|lowercase|lowercasefirst|lpad|lstat|lstrip|ltoh|macroexpand|map|map\!|mapfoldl|mapfoldr|mapreduce|mapslices|mark|match|max|maximum|maximum\!|maxintfloat|merge|merge\!|methods|min|minimum|minimum\!|minmax|missing|mkdir|mkpath|mktemp|mktempdir|mod|mod1|mod2pi|modf|mtime|muladd|mv|nameof|names|ncodeunits|ndigits|ndims|nextfloat|nextind|nextpow|nextprod|normpath|notify|ntoh|ntuple|numerator|objectid|occursin|oftype|one|ones|oneunit|open|operm|pairs|parent|parentindices|parentmodule|parse|partialsort|partialsort\!|partialsortperm|partialsortperm\!|pathof|permute\!|permutedims|permutedims\!|pi|pipeline|pointer|pointer_from_objref|pop\!|popdisplay|popfirst\!|position|powermod|precision|precompile|prepend\!|prevfloat|prevind|prevpow|print|println|printstyled|process_exited|process_running|prod|prod\!|promote|promote_rule|promote_shape|promote_type|propertynames|push\!|pushdisplay|pushfirst\!|put\!|pwd|rad2deg|rand|randn|range|rationalize|read|read\!|readavailable|readbytes\!|readchomp|readdir|readline|readlines|readlink|readuntil|real|realpath|redirect_stderr|redirect_stdin|redirect_stdout|redisplay|reduce|reenable_sigint|reim|reinterpret|relpath|rem|rem2pi|repeat|replace|replace\!|repr|reset|reshape|resize\!|rethrow|retry|reverse|reverse\!|reverseind|rm|rot180|rotl90|rotr90|round|rounding|rpad|rsplit|rstrip|run|schedule|searchsorted|searchsortedfirst|searchsortedlast|sec|secd|sech|seek|seekend|seekstart|selectdim|set_zero_subnormals|setdiff|setdiff\!|setenv|setindex\!|setprecision|setproperty\!|setrounding|show|showable|showerror|sign|signbit|signed|significand|similar|sin|sinc|sincos|sind|sinh|sinpi|size|sizehint\!|sizeof|skip|skipchars|skipmissing|sleep|something|sort|sort\!|sortperm|sortperm\!|sortslices|splice\!|split|splitdir|splitdrive|splitext|splitpath|sprint|sqrt|stacktrace|startswith|stat|stderr|stdin|stdout|step|stride|strides|string|strip|success|sum|sum\!|summary|supertype|symdiff|symdiff\!|symlink|systemerror|take\!|tan|tand|tanh|task_local_storage|tempdir|tempname|textwidth|thisind|time|time_ns|timedwait|titlecase|to_indices|touch|trailing_ones|trailing_zeros|transcode|transpose|trues|trunc|truncate|trylock|tryparse|typeintersect|typejoin|typemax|typemin|unescape_string|union|union\!|unique|unique\!|unlock|unmark|unsafe_copyto\!|unsafe_load|unsafe_pointer_to_objref|unsafe_read|unsafe_store\!|unsafe_string|unsafe_trunc|unsafe_wrap|unsafe_write|unsigned|uperm|uppercase|uppercasefirst|valtype|values|vcat|vec|view|wait|walkdir|which|widemul|widen|withenv|write|xor|yield|yieldto|zero|zeros|zip)|Broadcast\.(broadcast|broadcast\!|broadcast_axes|broadcastable|dotview)|Docs\.(doc)|GC\.|Iterators\.(countfrom|cycle|drop|enumerate|flatten|partition|product|repeated|rest|take|zip)|Libc\.(calloc|errno|flush_cstdio|free|gethostname|getpid|malloc|realloc|strerror|strftime|strptime|systemsleep|time|transcode)|MathConstants\.(catalan|e|eulergamma|golden|pi)|Meta\.(isexpr|quot|show_sexpr)|StackTraces\.(stacktrace)|Sys\.(cpu_info|cpu_summary|free_memory|isapple|isbsd|isdragonfly|isexecutable|isfreebsd|isjsvm|islinux|isnetbsd|isopenbsd|isunix|iswindows|loadavg|total_memory|uptime|which)|Threads\.(atomic_add\!|atomic_and\!|atomic_cas\!|atomic_fence|atomic_max\!|atomic_min\!|atomic_nand\!|atomic_or\!|atomic_sub\!|atomic_xchg\!|atomic_xor\!|nthreads|threadid)|Core\.(applicable|eval|fieldtype|getfield|ifelse|invoke|isa|isdefined|nfields|nothing|setfield\!|throw|tuple|typeassert|typeof|undef)*/

KEYWORD_OTHER1 = abstract|type|mutable|struct|primitive
KEYWORD_OTHER2 = baremodule|begin|const|end|export|function|global|import|let|local|macro|module|quote|return|struct|using
KEYWORD_OTHER = {KEYWORD_OTHER1} | {KEYWORD_OTHER2}

KEYWORD_CONTROL1 = break|catch|continue|do|else|elseif|finally|for|if|try|while
KEYWORD_CONTROL2 = where|in|isa
KEYWORD_CONTROL = {KEYWORD_CONTROL1} | {KEYWORD_CONTROL2}

CONSTANTS = true|false|nothing|missing|ℯ|pi|π|im|undef|NaN|NaN16|NaN32|NaN64|Inf|Inf16|Inf32|Inf64|ARGS|C_NULL|ENDIAN_BOM|ENV|LOAD_PATH|PROGRAM_FILE|STDERR|STDIN|STDOUT|VERSION

INTEGER_LITERAL = {DIGITS} | {HEX_INTEGER_LITERAL} | {BIN_INTEGER_LITERAL}
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = (b|r|v|raw)?\"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?
LONG_DOUBLE_QUOTED_STRING = (b|r|v|raw)?\"\"\" ~\"\"\"
SINGLE_BACKTICK_STRING = `([^\\`\r\n] | \\[^\r\n] | \\{CRLF})*`?

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

LINE_COMMENT = "#".*
BLOCK_COMMENT = "#=" ~"=#"

%%

<YYINITIAL> {

  {INTEGER_LITERAL} { return JuliaToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return JuliaToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return JuliaToken.DOUBLE_LITERAL; }

  {KEYWORD_OTHER} { return JuliaToken.KEYWORD_OTHER; }
  {KEYWORD_CONTROL} { return JuliaToken.KEYWORD_CONTROL; }

  {CONSTANTS} { return JuliaToken.CONSTANTS; }
  {OPERATOR} { return JuliaToken.OPERATOR; }

  /*{BASE_MODULE_FUNCS} { return JuliaToken.BASE_MODULE_FUNCS; }
  {BASE_MACROS} { return JuliaToken.BASE_MACROS; }
  {BASE_MODULES} { return JuliaToken.BASE_MODULES; }
  {BASE_FUNCS} { return JuliaToken.BASE_FUNCS; }*/
  {BASE_TYPES} { return JuliaToken.BASE_TYPES; }

  {LINE_COMMENT} { return JuliaToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return JuliaToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return JuliaToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return JuliaToken.SINGLE_QUOTED_STRING; }
  {LONG_DOUBLE_QUOTED_STRING} { return JuliaToken.LONG_DOUBLE_QUOTED_STRING; }
  {SINGLE_BACKTICK_STRING} { return JuliaToken.SINGLE_BACKTICK_STRING; }

  {IDENTIFIER} { return JuliaToken.IDENTIFIER; }
  {WHITESPACE} { return JuliaToken.WHITESPACE; }
}

[^] { return JuliaToken.BAD_CHARACTER; }

<<EOF>> { return JuliaToken.EOF; }