    const { useState, useEffect, useRef } = React;

    const CONFIG_VERSION = "3.5.0";
    const cloneJson = (value) => JSON.parse(JSON.stringify(value));
    const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    const DEFAULT_FILE_PATHS = {
        config: "defaults/config.json",
        styles: "defaults/styles.json",
        mentions: "defaults/mentions.json",
        presets: "defaults/presets.json"
    };
    const fetchDefaultJson = async (path) => {
        const response = await fetch(path, { cache: "no-store" });
        if (!response.ok) {
            throw new Error(`Could not load ${path} (${response.status})`);
        }
        return response.json();
    };

    const IconWrapper = ({ name, size, className }) => {
        const iconKey = name.split('-').map(part => part.charAt(0).toUpperCase() + part.slice(1)).join('');

        if (!window.lucide || !window.lucide.icons || !window.lucide.icons[iconKey]) {
            return <span style={{ width: size, height: size, display: 'inline-block' }} />;
        }

        try {
            if (typeof window.lucide.createElement === 'function') {
                const element = window.lucide.createElement(window.lucide.icons[iconKey]);
                element.setAttribute('width', size);
                element.setAttribute('height', size);
                if (className) element.setAttribute('class', className);
                element.setAttribute('stroke-width', '2');
                return <span dangerouslySetInnerHTML={{ __html: element.outerHTML }} style={{ display: 'inline-flex', verticalAlign: 'middle' }} />;
            }
        } catch (e) {
            console.error(e);
        }

        return null;
    };

    const STYLE_TYPES = [
        "<COLORS>","COLOR_HEX","COLOR_RAINBOW","COLOR_GRADIENT","COLOR_PRESET","COLOR_SHADOW","COLOR_TEAM",
        "<Formatting>","BOLD","ITALIC","UNDERLINE","STRIKETHROUGH","OBFUSCATED","FONT","CLEAR",
        "<Interaction>","CLICK_COMMAND_RUN","CLICK_COMMAND_SUGGEST","CLICK_COPY","HOVER_TEXT","HOVER_ITEM","URL","METADATA",
        "<Modification>","UPPER","LOWER","CAPITALIZE","REPLACE","MASK","PREFIX","SUFFIX",
        "<Advanced>","SHOW_ITEM","SHOW_INVENTORY","SHOW_ENDER_CHEST","ATLAS_PRESET","JSON","DISCORD_JSON","COMMAND_RUN","LOG","BUBBLE","BLOCK"
    ];

    const MENTION_TYPES = [
        "PLAYER", "TEAM", "INSIDE", "EVERYONE", "LUCK_PERMS_GROUP", "WORLD","CUSTOM"
    ];

    const SOUND_CATEGORIES = [
        "MASTER", "MUSIC", "RECORDS", "WEATHER", "BLOCKS", "HOSTILE", "NEUTRAL", "PLAYERS", "AMBIENT", "VOICE", "UI"
    ];

    const fieldBaseClass = "w-full rounded-2xl border border-slate-200/80 bg-white/85 px-4 py-3 text-sm text-slate-800 shadow-sm outline-none transition duration-200 placeholder:text-slate-400 focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100 dark:border-slate-700 dark:bg-slate-950/80 dark:text-slate-100 dark:placeholder:text-slate-500 dark:focus:border-indigo-500 dark:focus:ring-indigo-500/20";
    const sectionCardClass = "relative overflow-hidden rounded-[28px] border border-white/60 bg-white/85 shadow-soft backdrop-blur dark:border-slate-800/80 dark:bg-slate-900/80";
    const fieldLabelClass = "text-[11px] font-extrabold uppercase tracking-[0.2em] text-slate-700 dark:text-slate-200";
    const sectionLabelClass = "text-[11px] font-extrabold uppercase tracking-[0.22em] text-slate-700 dark:text-slate-200";

    const BrandMark = ({ size = 72, className = "" }) => {
        const outerStroke = "#E2E8F0";
        const bubbleStroke = "url(#bubbleGradConfig)";
        const sparkleStroke = "url(#sparkleGradConfig)";
        const sparkleFill = "url(#sparkleGradConfig)";
        const glowColor = "#F43F5E";
        const glowOpacity = "0.35";

        return (
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="-4 -4 32 32" width={size} height={size} fill="none" className={className}>
                <defs>
                    <linearGradient id="bubbleGradConfig" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stopColor="#4F46E5" />
                        <stop offset="100%" stopColor="#9333EA" />
                    </linearGradient>
                    <linearGradient id="sparkleGradConfig" x1="0%" y1="100%" x2="100%" y2="0%">
                        <stop offset="0%" stopColor="#F59E0B" />
                        <stop offset="100%" stopColor="#F43F5E" />
                    </linearGradient>
                    <linearGradient id="bubbleMonoConfig" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stopColor="#FFFFFF" />
                        <stop offset="100%" stopColor="#CBD5E1" />
                    </linearGradient>
                    <linearGradient id="sparkleMonoConfig" x1="0%" y1="100%" x2="100%" y2="0%">
                        <stop offset="0%" stopColor="#F8FAFC" />
                        <stop offset="100%" stopColor="#94A3B8" />
                    </linearGradient>
                    <filter id="glowConfig" x="-50%" y="-50%" width="200%" height="200%">
                        <feDropShadow dx="0" dy="0" stdDeviation="2.5" floodColor={glowColor} floodOpacity={glowOpacity} />
                    </filter>
                </defs>
                <path d="M8 8h12a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2h-1.5l-3.5 3v-3H8a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z" stroke={outerStroke} strokeWidth="1.5" fill="none" strokeLinejoin="round"/>
                <path d="M16 4H4a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h1.5v3.5L9 16h7a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z" stroke={bubbleStroke} strokeWidth="2" fill="none" strokeLinejoin="round" strokeLinecap="round"/>
                <path d="M6 9h7" stroke={bubbleStroke} strokeWidth="2" strokeLinecap="round"/>
                <path d="M6 13h4" stroke={bubbleStroke} strokeWidth="2" strokeLinecap="round"/>
                <path d="M19 1l1.5 3.5L24 6l-3.5 1.5L19 11l-1.5-3.5L14 6l3.5-1.5z" stroke={sparkleStroke} strokeWidth="1.8" fill={sparkleFill} strokeLinejoin="round" filter="url(#glowConfig)"/>
                <path d="M4 1l.7 1.8L6.5 3.5 4.7 4.2 4 6l-.7-1.8L1.5 3.5l1.8-.7z" stroke={sparkleStroke} strokeWidth="1.5" fill={sparkleFill} strokeLinejoin="round"/>
                <path d="M22 13l.5 1.5 1.5.5-1.5.5-.5 1.5-.5-1.5-1.5-.5 1.5-.5z" stroke={sparkleStroke} strokeWidth="1.5" fill={sparkleFill} strokeLinejoin="round"/>
            </svg>
        );
    };

    const Card = ({ children, className = "" }) => (
        <div className={`${sectionCardClass} ${className}`}>
            <div className="pointer-events-none absolute inset-x-8 top-0 h-px bg-gradient-to-r from-transparent via-white/80 to-transparent dark:via-slate-700/60" />
            {children}
        </div>
    );

    const SectionHeading = ({ eyebrow, title, description, icon, badge = null }) => (
        <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
            <div className="space-y-2">
                {eyebrow && <div className="text-xs font-semibold uppercase tracking-[0.24em] text-indigo-500">{eyebrow}</div>}
                <div className="flex items-center gap-3">
                    {icon && (
                        <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 text-white shadow-glow">
                            <IconWrapper name={icon} size={18} />
                        </div>
                    )}
                    <div className="flex flex-wrap items-center gap-3">
                        <h2 className="text-2xl font-black tracking-tight text-slate-900 dark:text-white">{title}</h2>
                        {badge}
                    </div>
                </div>
            </div>
        </div>
    );

    const CountBadge = ({ count, label = "items", tone = "indigo" }) => {
        const tones = {
            indigo: "bg-indigo-100 text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-300",
            amber: "bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-300",
            emerald: "bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300",
            slate: "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300"
        };

        return (
            <div className={`relative top-0.5 inline-flex items-center rounded-full px-3 py-1 text-xs font-bold ${tones[tone] || tones.indigo}`}>
                {count} {label}
            </div>
        );
    };

    const StatPill = ({ label, value, accent = "indigo" }) => {
        const accents = {
            indigo: "from-indigo-500/15 to-purple-500/15 text-indigo-700 dark:text-indigo-300",
            amber: "from-amber-400/20 to-rose-400/15 text-amber-700 dark:text-amber-300",
            emerald: "from-emerald-400/20 to-cyan-400/15 text-emerald-700 dark:text-emerald-300",
            slate: "from-slate-200/60 to-slate-100/20 text-slate-700 dark:from-slate-800 dark:to-slate-900 dark:text-slate-300"
        };

        return (
            <div className={`rounded-2xl border border-white/70 bg-gradient-to-br px-4 py-3 shadow-sm dark:border-slate-800 ${accents[accent] || accents.indigo}`}>
                <div className="text-[11px] font-semibold uppercase tracking-[0.22em] opacity-70">{label}</div>
                <div className="mt-1 text-lg font-black">{value}</div>
            </div>
        );
    };

    const Button = ({ onClick, children, variant = "primary", size = "md", className = "", disabled = false }) => {
        const baseStyles = "inline-flex items-center justify-center gap-2 rounded-2xl font-semibold transition duration-200 focus:outline-none focus:ring-4 disabled:opacity-50 disabled:cursor-not-allowed";
        const variants = {
            primary: "bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-glow hover:brightness-105 focus:ring-indigo-200 dark:focus:ring-indigo-500/25",
            secondary: "border border-slate-200 bg-white/90 text-slate-700 shadow-sm hover:border-indigo-200 hover:bg-indigo-50 focus:ring-indigo-100 dark:border-slate-700 dark:bg-slate-900/90 dark:text-slate-200 dark:hover:border-indigo-500/40 dark:hover:bg-slate-800 dark:focus:ring-indigo-500/20",
            danger: "border border-red-200 bg-red-50 text-red-700 shadow-sm hover:bg-red-100 focus:ring-red-100 dark:border-red-900/50 dark:bg-red-950/40 dark:text-red-300 dark:hover:bg-red-950/60 dark:focus:ring-red-500/20",
            ghost: "text-slate-500 hover:bg-slate-100 hover:text-slate-800 focus:ring-slate-100 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-slate-100 dark:focus:ring-slate-700"
        };
        const sizes = {
            sm: "px-3 py-2 text-xs",
            md: "px-4 py-2.5 text-sm",
            icon: "h-11 w-11"
        };

        return (
            <button
                type="button"
                onClick={onClick}
                className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
                disabled={disabled}
            >
                {children}
            </button>
        );
    };

    const DeleteButton = ({ onDelete, label = "Delete", size = "sm", className = "" }) => {
        const [isConfirming, setIsConfirming] = useState(false);

        useEffect(() => {
            let timeout;
            if (isConfirming) {
                timeout = setTimeout(() => setIsConfirming(false), 3000);
            }
            return () => clearTimeout(timeout);
        }, [isConfirming]);

        return (
            <Button
                variant="danger"
                size={size}
                className={className}
                onClick={(e) => {
                    e.stopPropagation();
                    if (isConfirming) {
                        onDelete();
                        setIsConfirming(false);
                    } else {
                        setIsConfirming(true);
                    }
                }}
            >
                {isConfirming ? "Confirm?" : label}
            </Button>
        );
    };

    const AddGroupInput = ({ onAdd, placeholder }) => {
        const [value, setValue] = useState("");

        const handleAdd = () => {
            if (value.trim()) {
                onAdd(value.trim());
                setValue("");
            }
        };

        return (
            <div className="flex w-full flex-col gap-3 sm:flex-row sm:items-center md:w-auto">
                <input
                    type="text"
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                    placeholder={placeholder}
                    className={`${fieldBaseClass} sm:min-w-[280px]`}
                    onKeyDown={(e) => e.key === 'Enter' && handleAdd()}
                />
                <Button size="md" onClick={handleAdd} disabled={!value.trim()} className="sm:shrink-0">
                    <IconWrapper name="plus" size={16} /> Add
                </Button>
            </div>
        );
    };

    const Input = ({ label, value, onChange, placeholder, type = "text", className = "", step = "any" }) => (
        <div className={`flex flex-col gap-2 ${className}`}>
            {label && <label className={fieldLabelClass}>{label}</label>}
            <input
                type={type}
                step={step}
                value={value ?? ""}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder}
                className={fieldBaseClass}
            />
        </div>
    );

    const Combobox = ({ label, value, onChange, options, className = "", placeholder = "" }) => {
        const [listId] = useState(() => "datalist-" + Math.random().toString(36).substr(2, 9));

        return (
            <div className={`flex flex-col gap-2 ${className}`}>
                {label && <label className={fieldLabelClass}>{label}</label>}
                <input
                    list={listId}
                    value={value ?? ""}
                    onChange={(e) => onChange(e.target.value)}
                    className={fieldBaseClass}
                    placeholder={placeholder}
                />
                <datalist id={listId}>
                    {options.map((opt) => (
                        <option key={opt} value={opt} />
                    ))}
                </datalist>
            </div>
        );
    };

    const ColorPicker = ({ label, value, onChange }) => (
        <div className="flex flex-col gap-2">
            {label && <label className={fieldLabelClass}>{label}</label>}
            <div className="flex items-center gap-3 rounded-2xl border border-slate-200/80 bg-white/85 p-3 shadow-sm dark:border-slate-700 dark:bg-slate-950/80">
                <input
                    type="color"
                    value={value || "#000000"}
                    onChange={(e) => onChange(e.target.value)}
                    className="h-12 w-12 cursor-pointer rounded-2xl border border-slate-200 bg-transparent p-1 dark:border-slate-700"
                />
                <input
                    type="text"
                    value={value || ""}
                    onChange={(e) => onChange(e.target.value)}
                    className="min-w-0 flex-1 border-0 bg-transparent px-1 text-sm font-semibold tracking-wide text-slate-700 outline-none placeholder:text-slate-400 dark:text-slate-100 dark:placeholder:text-slate-500"
                    placeholder="#RRGGBB"
                />
            </div>
        </div>
    );

    const ToggleCard = ({ id, label, description, checked, onChange }) => (
        <label htmlFor={id} className="group flex cursor-pointer items-start gap-4 rounded-3xl border border-slate-200/80 bg-white/80 p-4 shadow-sm transition hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md dark:border-slate-700 dark:bg-slate-950/60 dark:hover:border-indigo-500/30">
            <div className="pt-1">
                <input
                    id={id}
                    type="checkbox"
                    checked={checked}
                    onChange={(e) => onChange(e.target.checked)}
                    className="h-5 w-5 cursor-pointer rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
                />
            </div>
            <div className="min-w-0 flex-1">
                <div className="font-semibold text-slate-800 dark:text-slate-100">{label}</div>
            </div>
            <div className={`mt-0.5 rounded-full px-3 py-1 text-xs font-bold ${checked ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-300' : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'}`}>
                {checked ? 'ON' : 'OFF'}
            </div>
        </label>
    );

    const InlineCheckbox = ({ id, label, checked, onChange, className = "" }) => (
        <label htmlFor={id} className={`flex min-h-[48px] w-full cursor-pointer items-center gap-3 rounded-2xl border border-slate-200/80 bg-white/80 px-4 py-3 shadow-sm transition hover:border-indigo-200 dark:border-slate-700 dark:bg-slate-950/60 dark:hover:border-indigo-500/30 ${className}`}>
            <input
                id={id}
                type="checkbox"
                checked={checked}
                onChange={(e) => onChange(e.target.checked)}
                className="h-4 w-4 cursor-pointer rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
            />
            <span className="text-sm font-semibold text-slate-800 dark:text-slate-100">{label}</span>
            <span className={`ml-auto rounded-full px-2.5 py-1 text-[11px] font-bold ${checked ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-300' : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'}`}>
                {checked ? 'ON' : 'OFF'}
            </span>
        </label>
    );

    const EmptyState = ({ icon = "inbox", title, description }) => (
        <div className="rounded-[28px] border border-dashed border-slate-300/80 bg-slate-50/70 px-6 py-10 text-center dark:border-slate-700 dark:bg-slate-900/40">
            <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-white text-indigo-500 shadow-sm dark:bg-slate-900 dark:text-indigo-300">
                <IconWrapper name={icon} size={20} />
            </div>
            <h3 className="mt-4 text-lg font-bold text-slate-800 dark:text-white">{title}</h3>
            {description && <p className="mt-2 text-sm text-slate-500 dark:text-slate-400">{description}</p>}
        </div>
    );

    const StringListCard = ({
        eyebrow,
        title,
        icon,
        count,
        items,
        onAdd,
        onRemove,
        placeholder,
        emptyTitle,
        emptyDescription,
        enableProfileLookup = false,
        loadEntryPreview = null,
    }) => {
        const [selectedEntry, setSelectedEntry] = useState(null);
        const [previewState, setPreviewState] = useState({ status: "idle", data: null, error: "" });

        const handleEntryClick = async (entry) => {
            if (!enableProfileLookup || typeof loadEntryPreview !== "function") return;

            if (selectedEntry === entry && previewState.status !== "loading") {
                setSelectedEntry(null);
                setPreviewState({ status: "idle", data: null, error: "" });
                return;
            }

            setSelectedEntry(entry);
            setPreviewState({ status: "loading", data: null, error: "" });

            try {
                const profile = await loadEntryPreview(entry);
                setPreviewState({ status: "success", data: profile, error: "" });
            } catch (error) {
                const normalizedUuid = entry.replace(/-/g, "");
                setPreviewState({
                    status: "error",
                    data: {
                        uuid: entry,
                        normalizedUuid,
                        name: "Unknown Player",
                        headUrl: `https://mc-heads.net/head/${normalizedUuid}/96`,
                        avatarUrl: `https://mc-heads.net/avatar/${normalizedUuid}/64`
                    },
                    error: error?.message || "Could not load this player from the profile API."
                });
            }
        };

        return (
            <Card className="p-6 md:p-7">
                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                        <SectionHeading
                            eyebrow={eyebrow}
                            title={title}
                            icon={icon}
                            badge={<CountBadge count={count} label="entries" />}
                        />
                        <AddGroupInput onAdd={onAdd} placeholder={placeholder} />
                    </div>

                    {count === 0 ? (
                        <EmptyState icon={icon} title={emptyTitle} description={emptyDescription} />
                    ) : (
                        <>
                            <div className="flex flex-wrap gap-3">
                                {items.map((entry) => {
                                    const isSelected = selectedEntry === entry;
                                    return (
                                        <div
                                            key={entry}
                                            className={`inline-flex items-center gap-2 rounded-full border px-4 py-2 text-sm shadow-sm dark:bg-slate-950/60 ${
                                                isSelected
                                                    ? "border-indigo-300 bg-indigo-50 text-indigo-700 dark:border-indigo-500/40 dark:bg-indigo-500/10 dark:text-indigo-200"
                                                    : "border-slate-200/80 bg-slate-50/90 dark:border-slate-700"
                                            }`}
                                        >
                                            <button
                                                type="button"
                                                onClick={() => handleEntryClick(entry)}
                                                className={`inline-flex items-center gap-2 rounded-full text-left transition ${enableProfileLookup ? "cursor-pointer hover:text-indigo-600 dark:hover:text-indigo-300" : "cursor-default"}`}
                                            >
                                                {enableProfileLookup && <IconWrapper name="contact" size={14} />}
                                                <span className="font-mono text-slate-700 dark:text-slate-200">{entry}</span>
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => onRemove(entry)}
                                                className="text-slate-400 transition hover:text-red-500"
                                            >
                                                <IconWrapper name="x" size={14} />
                                            </button>
                                        </div>
                                    );
                                })}
                            </div>

                            {enableProfileLookup && selectedEntry && (
                                <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/80 p-4 shadow-sm dark:border-slate-700 dark:bg-slate-900/40">
                                    <div className="mb-3 flex items-center gap-3">
                                        <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 text-white shadow-glow">
                                            <IconWrapper name="contact" size={16} />
                                        </div>
                                        <div>
                                            <div className="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-400">Player Preview</div>
                                            <div className="text-sm font-semibold text-slate-800 dark:text-slate-100">Click the UUID again to collapse.</div>
                                        </div>
                                    </div>

                                    {previewState.status === "loading" && (
                                        <div className="flex items-center gap-4 rounded-2xl bg-white/80 p-4 dark:bg-slate-950/50">
                                            <div className="h-16 w-16 animate-pulse rounded-2xl bg-slate-200 dark:bg-slate-800" />
                                            <div className="flex-1 space-y-2">
                                                <div className="h-4 w-32 animate-pulse rounded bg-slate-200 dark:bg-slate-800" />
                                                <div className="h-3 w-56 animate-pulse rounded bg-slate-200 dark:bg-slate-800" />
                                            </div>
                                        </div>
                                    )}

                                    {previewState.status !== "loading" && previewState.data && (
                                        <div className="flex flex-col gap-4 rounded-2xl bg-white/80 p-4 dark:bg-slate-950/50 md:flex-row md:items-center">
                                            <img
                                                src={previewState.data.headUrl}
                                                alt={`${previewState.data.name} head`}
                                                className="h-20 w-20 rounded-2xl border border-slate-200 object-cover shadow-sm dark:border-slate-700"
                                            />
                                            <div className="min-w-0 flex-1">
                                                <div className="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-400">Player Name</div>
                                                <div className="truncate text-xl font-black text-slate-900 dark:text-white">{previewState.data.name}</div>
                                                <div className="mt-3 text-[11px] font-bold uppercase tracking-[0.2em] text-slate-400">UUID</div>
                                                <div className="mt-1 break-all font-mono text-sm text-slate-600 dark:text-slate-300">{previewState.data.uuid}</div>
                                                {previewState.status === "error" && (
                                                    <div className="mt-3 rounded-2xl border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-700 dark:border-amber-900/40 dark:bg-amber-950/30 dark:text-amber-300">
                                                        {previewState.error}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </>
                    )}
                </div>
            </Card>
        );
    };

    const TabButton = ({ active, onClick, icon, label, compactLabel }) => (
        <button
            type="button"
            onClick={onClick}
            className={`group inline-flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-semibold transition duration-200 whitespace-nowrap ${
                active
                    ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-glow'
                    : 'bg-white/75 text-slate-600 hover:bg-white hover:text-slate-900 dark:bg-slate-900/70 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-slate-100'
            }`}
        >
            <span className={`flex h-8 w-8 items-center justify-center rounded-xl ${active ? 'bg-white/20' : 'bg-slate-100 text-indigo-500 dark:bg-slate-800 dark:text-indigo-300'}`}>
                <IconWrapper name={icon} size={16} />
            </span>
            <span className="hidden sm:inline">{label}</span>
            <span className="sm:hidden">{compactLabel || label}</span>
        </button>
    );

    const Accordion = ({ title, children, defaultOpen = false, onDelete }) => {
        const [isOpen, setIsOpen] = useState(defaultOpen);
        return (
            <div className="overflow-hidden rounded-[26px] border border-slate-200/80 bg-white/70 shadow-sm dark:border-slate-700 dark:bg-slate-950/40">
                <div
                    className="flex items-center justify-between gap-3 px-4 py-4 cursor-pointer select-none bg-gradient-to-r from-slate-50 to-white dark:from-slate-900/80 dark:to-slate-900/30"
                    onClick={() => setIsOpen(!isOpen)}
                >
                    <div className="flex min-w-0 items-center gap-3 overflow-hidden">
                        <div className={`flex h-9 w-9 items-center justify-center rounded-xl transition ${isOpen ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-300' : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'}`}>
                            {isOpen ? <IconWrapper name="chevron-down" size={16} className="flex-shrink-0" /> : <IconWrapper name="chevron-right" size={16} className="flex-shrink-0" />}
                        </div>
                        <div className="min-w-0">
                            <div className={sectionLabelClass}>Rule</div>
                            <span className="block truncate text-sm font-semibold text-slate-800 dark:text-slate-100">{title}</span>
                        </div>
                    </div>
                    {onDelete && (
                        <div onClick={(e) => e.stopPropagation()} className="flex-shrink-0 ml-2">
                            <DeleteButton onDelete={onDelete} label={<IconWrapper name="trash-2" size={14} />} />
                        </div>
                    )}
                </div>
                {isOpen && (
                    <div className="border-t border-slate-200/80 bg-white/80 p-5 dark:border-slate-700 dark:bg-slate-950/50">
                        {children}
                    </div>
                )}
            </div>
        );
    };

    const StyleActionsEditor = ({ actions, onChange }) => (
        <div className="space-y-3 mt-2">
            <label className={fieldLabelClass}>Style Actions</label>
            {actions.map((action, idx) => (
                <div key={idx} className="grid gap-3 rounded-3xl border border-slate-200/80 bg-slate-50/80 p-3 dark:border-slate-700 dark:bg-slate-900/50 md:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_auto] md:items-end">
                    <Combobox
                        label={idx === 0 ? "Style Type" : "Action Type"}
                        value={action.styleType}
                        options={STYLE_TYPES}
                        onChange={(v) => {
                            const newActions = [...actions];
                            newActions[idx] = { ...newActions[idx], styleType: v };
                            onChange(newActions);
                        }}
                        placeholder="Select..."
                    />
                    <Input
                        label={idx === 0 ? "Preset / Option" : "Option"}
                        value={action.preset}
                        onChange={(v) => {
                            const newActions = [...actions];
                            newActions[idx] = { ...newActions[idx], preset: v };
                            onChange(newActions);
                        }}
                        placeholder="(Optional)"
                    />
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => {
                            const newActions = actions.filter((_, i) => i !== idx);
                            onChange(newActions);
                        }}
                    >
                        <IconWrapper name="trash-2" size={16} />
                    </Button>
                </div>
            ))}
            <Button
                variant="secondary"
                size="md"
                className="w-full"
                onClick={() => onChange([...(actions || []), { styleType: "", preset: "" }])}
            >
                <IconWrapper name="plus" size={14} /> Add Style Action
            </Button>
        </div>
    );

    const MentionActionsEditor = ({ actions, onChange }) => (
        <div className="space-y-3 mt-2">
            <label className={fieldLabelClass}>Mention Actions</label>
            {actions.map((action, idx) => (
                <div key={idx} className="grid gap-3 rounded-3xl border border-slate-200/80 bg-slate-50/80 p-3 dark:border-slate-700 dark:bg-slate-900/50 md:grid-cols-[minmax(0,1.15fr)_minmax(0,1fr)_auto] md:items-end">
                    <Combobox
                        label={idx === 0 ? "Mention Type" : "Action Type"}
                        value={action.mentionType}
                        options={MENTION_TYPES}
                        onChange={(v) => {
                            const newActions = [...actions];
                            newActions[idx] = { ...newActions[idx], mentionType: v };
                            onChange(newActions);
                        }}
                        placeholder="Select..."
                    />
                    <Input
                        label={idx === 0 ? "Preset / Value" : "Value"}
                        value={action.preset}
                        onChange={(v) => {
                            const newActions = [...actions];
                            newActions[idx] = { ...newActions[idx], preset: v };
                            onChange(newActions);
                        }}
                        placeholder="e.g. 64 or team_name"
                    />
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => {
                            const newActions = actions.filter((_, i) => i !== idx);
                            onChange(newActions);
                        }}
                    >
                        <IconWrapper name="trash-2" size={16} />
                    </Button>
                </div>
            ))}
            <Button
                variant="secondary"
                size="md"
                className="w-full"
                onClick={() => onChange([...(actions || []), { mentionType: "", preset: "" }])}
            >
                <IconWrapper name="plus" size={14} /> Add Mention Action
            </Button>
        </div>
    );

    const sortStylingRuleEntries = (styleRules) => {
        const priority = {
            "embellish-chat.chat": 0,
            "embellish-chat.command_argument": 1,
        };

        return Object.entries(styleRules || {}).sort(([a], [b]) => {
            const aPriority = priority[a] ?? 999;
            const bPriority = priority[b] ?? 999;
            if (aPriority !== bPriority) return aPriority - bPriority;
            return a.localeCompare(b);
        });
    };

    const GlobalView = ({
      config,
      presets,
      updateGlobal,
      addPreset,
      removePreset,
      updatePreset,
      addAtlasPreset,
      removeAtlasPreset,
      updateAtlasPreset,
      addWhitelist,
      removeWhitelist,
      addPrefix,
      removePrefix,
      updatePrefix,
      addBannedPlayer,
      removeBannedPlayer,
      addNotificationOffPlayer,
      removeNotificationOffPlayer,
      fetchPlayerProfile
    }) => {
      const colorPresetCount = Object.keys(presets.color || {}).length;
      const atlasPresetCount = Object.keys(presets.atlas || {}).length;
      const whitelistCount = (presets.whitelist || []).length;
      const prefixCount = Object.keys(presets.prefix || {}).length;
      const bannedPlayerCount = (config.banned_players || []).length;
      const notificationOffPlayerCount = (config.notify_off_players || []).length;

      return (
        <div className="space-y-8">
          <Card className="p-6 md:p-8">
            <div className="absolute right-0 top-0 h-40 w-40 rounded-full bg-gradient-to-br from-indigo-100 via-purple-100 to-pink-100 blur-3xl opacity-70 dark:from-indigo-900/40 dark:via-purple-900/20 dark:to-pink-900/20" />
            <div className="relative space-y-8">
              <SectionHeading
                eyebrow="Global"
                title="General Settings"
                icon="settings"
              />

              <div className="grid gap-4 md:grid-cols-2">
                <div className="flex flex-col gap-2 md:col-span-2">
                  <label className={fieldLabelClass}>Mod Version</label>
                  <div className="rounded-2xl border border-slate-200/80 bg-slate-100/80 px-4 py-3 text-sm font-semibold text-slate-700 shadow-sm dark:border-slate-700 dark:bg-slate-900/70 dark:text-slate-200">
                    {config.version || CONFIG_VERSION}
                  </div>
                  <p className="text-xs text-slate-500 dark:text-slate-400">`ConfigManager.readConfig()` only loads configs that match the current mod version, so this generator keeps the value read-only.</p>
                </div>
                <Input
                  label="Option Delimiter"
                  value={config.delimiter}
                  onChange={(v) => updateGlobal("delimiter", v)}
                  placeholder=","
                />
                <Input
                  label="Timestamp Format"
                  value={config.timestamp}
                  onChange={(v) => updateGlobal("timestamp", v)}
                  placeholder="yyyy-MM-dd HH:mm:ss"
                />
                <Input
                  label="Command Alias"
                  value={config.command_alias || ""}
                  onChange={(v) => updateGlobal("command_alias", v)}
                  placeholder="Leave empty to keep /embellish-chat only"
                />
              </div>

              <div className="grid gap-6 lg:grid-cols-2">
                <div className="space-y-4 rounded-[28px] border border-slate-200/80 bg-slate-50/80 p-5 dark:border-slate-700 dark:bg-slate-900/50">
                  <div className="flex items-center gap-3">
                    <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 text-white shadow-glow">
                      <IconWrapper name="palette" size={18} />
                    </div>
                    <div>
                      <div className="text-sm font-bold text-slate-800 dark:text-white">Colors</div>
                    </div>
                  </div>
                  <div className="grid gap-4">
                    <ColorPicker
                      label="URL Color"
                      value={config.url_color}
                      onChange={(v) => updateGlobal("url_color", v)}
                    />
                    <ColorPicker
                      label="Fallback Team Color"
                      value={config.team_color}
                      onChange={(v) => updateGlobal("team_color", v)}
                    />
                  </div>
                </div>

                <div className="space-y-4 rounded-[28px] border border-slate-200/80 bg-slate-50/80 p-5 dark:border-slate-700 dark:bg-slate-900/50">
                  <div className="flex items-center gap-3">
                    <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-amber-400 to-rose-500 text-white shadow-glow">
                      <IconWrapper name="toggle-left" size={18} />
                    </div>
                    <div>
                      <div className="text-sm font-bold text-slate-800 dark:text-white">Toggles</div>
                    </div>
                  </div>
                  <div className="space-y-3">
                    <ToggleCard
                      id="notifEnable"
                      label="Allow notification command"
                      checked={config.notify_command_enabled}
                      onChange={(checked) => updateGlobal("notify_command_enabled", checked)}
                    />
                    <ToggleCard
                      id="notifyMention"
                      label="Enable mention notifications"
                      checked={config.notify_mention_enabled}
                      onChange={(checked) => updateGlobal("notify_mention_enabled", checked)}
                    />
                    <ToggleCard
                      id="disableVanillaChatFormat"
                      label="Disable vanilla chat format"
                      checked={config.disable_vanilla_chat_format}
                      onChange={(checked) => updateGlobal("disable_vanilla_chat_format", checked)}
                    />
                  </div>
                </div>
              </div>
            </div>
          </Card>

          <div className="grid gap-6">
             <Card className="p-6 md:p-7">
              <div className="flex flex-col gap-5">
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <SectionHeading
                    eyebrow="Presets"
                    title="Color Presets"
                    icon="palette"
                    badge={<CountBadge count={colorPresetCount} label="presets" />}
                  />
                  <AddGroupInput onAdd={addPreset} placeholder="New Preset Name" />
                </div>

                {colorPresetCount === 0 ? (
                  <EmptyState icon="palette" title="No color presets" />
                ) : (
                  <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
                    {Object.entries(presets.color || {}).map(([name, color]) => (
                      <div
                        key={name}
                        className="flex items-center gap-3 rounded-[24px] border border-slate-200/80 bg-slate-50/85 p-4 shadow-sm dark:border-slate-700 dark:bg-slate-950/50"
                      >
                        <input
                          type="color"
                          value={color}
                          onChange={(e) => updatePreset(name, e.target.value)}
                          className="h-12 w-12 cursor-pointer rounded-2xl border border-slate-200 bg-transparent p-1 dark:border-slate-700"
                        />
                        <div className="min-w-0 flex-1">
                          <div className="truncate text-sm font-bold text-slate-800 dark:text-slate-100">{name}</div>
                          <div className="mt-1 font-mono text-xs text-slate-500 dark:text-slate-400">{color}</div>
                        </div>
                        <Button variant="ghost" size="icon" onClick={() => removePreset(name)}>
                          <IconWrapper name="trash-2" size={16} />
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </Card>

            <Card className="p-6 md:p-7">
              <div className="flex flex-col gap-5">
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <SectionHeading
                    eyebrow="Assets"
                    title="Atlas Presets"
                    icon="image"
                    badge={<CountBadge count={atlasPresetCount} label="presets" />}
                  />
                  <AddGroupInput onAdd={addAtlasPreset} placeholder="New Atlas Preset Name" />
                </div>

                {atlasPresetCount === 0 ? (
                  <EmptyState icon="image" title="No atlas presets" />
                ) : (
                  <div className="grid grid-cols-1 gap-4 lg:grid-cols-2 xl:grid-cols-3">
                    {Object.entries(presets.atlas || {}).map(([name, data]) => (
                      <div
                        key={name}
                        className="rounded-[24px] border border-slate-200/80 bg-slate-50/85 p-4 shadow-sm dark:border-slate-700 dark:bg-slate-950/50"
                      >
                        <div className="mb-4 flex items-center justify-between gap-3">
                          <div>
                            <div className="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-400">Preset</div>
                            <span className="text-sm font-bold text-slate-800 dark:text-slate-100">{name}</span>
                          </div>
                          <Button variant="ghost" size="icon" onClick={() => removeAtlasPreset(name)}>
                            <IconWrapper name="trash-2" size={16} />
                          </Button>
                        </div>
                        <div className="space-y-3">
                          <Input
                            label="Atlas"
                            value={data.atlas}
                            onChange={(v) => updateAtlasPreset(name, "atlas", v)}
                            placeholder="minecraft:blocks"
                          />
                          <Input
                            label="Sprite"
                            value={data.sprite}
                            onChange={(v) => updateAtlasPreset(name, "sprite", v)}
                            placeholder="minecraft:block/campfire_fire"
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </Card>
          </div>

          <div className="grid gap-6">
            <Card className="p-6 md:p-7">
              <div className="flex flex-col gap-5">
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <SectionHeading
                    eyebrow="Security"
                    title="Whitelist"
                    icon="shield"
                    badge={<CountBadge count={whitelistCount} label="entries" />}
                  />
                  <AddGroupInput onAdd={addWhitelist} placeholder="New Whitelist Entry" />
                </div>

                {whitelistCount === 0 ? (
                  <EmptyState icon="shield-off" title="No whitelist entries" />
                ) : (
                  <div className="flex flex-wrap gap-3">
                    {(presets.whitelist || []).map((entry) => (
                      <div
                        key={entry}
                        className="inline-flex items-center gap-2 rounded-full border border-slate-200/80 bg-slate-50/90 px-4 py-2 text-sm shadow-sm dark:border-slate-700 dark:bg-slate-950/60"
                      >
                        <span className="font-mono text-slate-700 dark:text-slate-200">{entry}</span>
                        <button
                          type="button"
                          onClick={() => removeWhitelist(entry)}
                          className="text-slate-400 transition hover:text-red-500"
                        >
                          <IconWrapper name="x" size={14} />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </Card>

            <Card className="p-6 md:p-7">
              <div className="flex flex-col gap-5">
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <SectionHeading
                    eyebrow="Formatting"
                    title="Prefix Components"
                    icon="text-quote"
                    badge={<CountBadge count={prefixCount} label="entries" />}
                  />
                  <AddGroupInput onAdd={addPrefix} placeholder="New Prefix Key" />
                </div>

                {prefixCount === 0 ? (
                  <EmptyState icon="text-quote" title="No prefix entries" />
                ) : (
                  <div className="grid gap-4 md:grid-cols-2">
                    {Object.entries(presets.prefix || {}).map(([name, value]) => (
                      <div
                        key={name}
                        className="rounded-[24px] border border-slate-200/80 bg-slate-50/85 p-4 shadow-sm dark:border-slate-700 dark:bg-slate-950/50"
                      >
                        <div className="mb-4 flex items-center justify-between gap-3">
                          <div>
                            <div className="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-400">Key</div>
                            <span className="font-mono text-sm font-bold text-slate-800 dark:text-slate-100">{name}</span>
                          </div>
                          <Button variant="ghost" size="icon" onClick={() => removePrefix(name)}>
                            <IconWrapper name="trash-2" size={16} />
                          </Button>
                        </div>
                        <Input
                          label="Component Value"
                          value={value}
                          onChange={(v) => updatePrefix(name, v)}
                          placeholder="e.g. [Admin]"
                        />
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </Card>
          </div>

          <div className="grid gap-6">
             <StringListCard
               eyebrow="Players"
               title="Banned Player UUIDs"
               icon="user"
               count={bannedPlayerCount}
               items={config.banned_players || []}
               onAdd={addBannedPlayer}
               onRemove={removeBannedPlayer}
               placeholder="UUID (e.g. 123e4567-e89b-12d3-a456-426614174000)"
               emptyTitle="No banned player UUIDs"
               emptyDescription="Matches the `banned_players` field in `config.json`."
               enableProfileLookup={true}
               loadEntryPreview={fetchPlayerProfile}
             />
             <StringListCard
               eyebrow="Notifications"
               title="Notification-Off Player UUIDs"
               icon="users"
               count={notificationOffPlayerCount}
               items={config.notify_off_players || []}
               onAdd={addNotificationOffPlayer}
               onRemove={removeNotificationOffPlayer}
               placeholder="UUID (e.g. 123e4567-e89b-12d3-a456-426614174000)"
               emptyTitle="No notification opt-out UUIDs"
               emptyDescription="Matches the `notify_off_players` field in `config.json`."
               enableProfileLookup={true}
               loadEntryPreview={fetchPlayerProfile}
             />
           </div>
        </div>
      );
    };

    const StylingView = ({ config, addPermissionGroup, removePermissionGroup, updateRuleList }) => (
        <div className="space-y-8">
            <Card className="p-6 md:p-8">
                <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
                    <SectionHeading
                        eyebrow="Styles"
                        title="Styling Rules"
                        icon="message-square"
                    />
                    <AddGroupInput
                        onAdd={(name) => addPermissionGroup('style_rules', name)}
                        placeholder="Permission Node (e.g. embellish-chat.vip)"
                    />
                </div>
            </Card>

            {sortStylingRuleEntries(config.style_rules).map(([permKey, rules]) => (
                <Card key={permKey} className="p-6">
                    <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                        <div>
                            <div className="text-[11px] font-bold uppercase tracking-[0.22em] text-indigo-500">Permission Group</div>
                            <div className="mt-1 flex flex-wrap items-center gap-3">
                                <h3 className="break-all font-mono text-lg font-black text-slate-900 dark:text-white">{permKey}</h3>
                                <CountBadge count={rules.length} label={rules.length === 1 ? "rule" : "rules"} tone="indigo" />
                            </div>
                        </div>
                        <div className="flex items-center gap-3">
                            <DeleteButton
                                onDelete={() => removePermissionGroup('style_rules', permKey)}
                                label="Delete Group"
                            />
                        </div>
                    </div>

                    <div className="space-y-4">
                        {rules.length === 0 && (
                            <EmptyState icon="list-plus" title="No rules yet" />
                        )}
                        {rules.map((rule, idx) => (
                            <Accordion
                                key={idx}
                                title={`Rule #${idx + 1}: ${rule.pattern || '(Empty Pattern)'}`}
                                defaultOpen={false}
                                onDelete={() => {
                                    const newRules = rules.filter((_, i) => i !== idx);
                                    updateRuleList('style_rules', permKey, newRules);
                                }}
                            >
                                <div className="space-y-4">
                                    <Input
                                        label="Regex Pattern"
                                        value={rule.pattern}
                                        onChange={(v) => {
                                            const newRules = [...rules];
                                            newRules[idx] = { ...newRules[idx], pattern: v };
                                            updateRuleList('style_rules', permKey, newRules);
                                        }}
                                        className="font-mono text-sm"
                                        placeholder="e.g. \*\*([^\*]+)\*\*()"
                                    />
                                    <Input
                                        label="Comment (/embellish-chat help style)"
                                        value={rule.comment || ""}
                                        onChange={(v) => {
                                            const newRules = [...rules];
                                            newRules[idx] = { ...newRules[idx], comment: v };
                                            updateRuleList('style_rules', permKey, newRules);
                                        }}
                                        placeholder="<blue><b>Pattern</b></blue>: **Text** ..."
                                    />
                                    <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/70 p-4 dark:border-slate-700 dark:bg-slate-900/30">
                                        <StyleActionsEditor
                                            actions={rule.styles || []}
                                            onChange={(newActions) => {
                                                const newRules = [...rules];
                                                newRules[idx] = { ...newRules[idx], styles: newActions };
                                                updateRuleList('style_rules', permKey, newRules);
                                            }}
                                        />
                                    </div>
                                </div>
                            </Accordion>
                        ))}
                        <Button
                            className="w-full mt-2"
                            variant="secondary"
                            onClick={() => {
                                const newRules = [...rules, { pattern: "", styles: [], comment: "" }];
                                updateRuleList('style_rules', permKey, newRules);
                            }}
                        >
                            <IconWrapper name="plus" size={16} /> Add Rule to {permKey}
                        </Button>
                    </div>
                </Card>
            ))}
        </div>
    );

    const MentionView = ({ config, addPermissionGroup, removePermissionGroup, updateRuleList }) => {
        const updateSound = (rules, idx, field, value) => {
            const newRules = [...rules];
            const currentSound = newRules[idx].sound || { id: "", category: "UI", volume: 1.0, pitch: 1.0 };
            newRules[idx] = {
                ...newRules[idx],
                sound: { ...currentSound, [field]: value }
            };
            return newRules;
        };

        return (
            <div className="space-y-8">
                <Card className="p-6 md:p-8">
                    <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
                        <SectionHeading
                            eyebrow="Mentions"
                            title="Mention Rules"
                            icon="at-sign"
                        />
                        <AddGroupInput
                            onAdd={(name) => addPermissionGroup('mention_rules', name)}
                            placeholder="Permission Node (e.g. embellish-chat.vip)"
                        />
                    </div>
                </Card>

                {Object.entries(config.mention_rules).map(([permKey, rules]) => (
                    <Card key={permKey} className="p-6">
                        <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                            <div>
                                <div className="text-[11px] font-bold uppercase tracking-[0.22em] text-indigo-500">Permission Group</div>
                                <div className="mt-1 flex flex-wrap items-center gap-3">
                                    <h3 className="break-all font-mono text-lg font-black text-slate-900 dark:text-white">{permKey}</h3>
                                    <CountBadge count={rules.length} label={rules.length === 1 ? "rule" : "rules"} tone="amber" />
                                </div>
                            </div>
                            <div className="flex items-center gap-3">
                                <DeleteButton
                                    onDelete={() => removePermissionGroup('mention_rules', permKey)}
                                    label="Delete Group"
                                />
                            </div>
                        </div>

                        <div className="space-y-4">
                            {rules.length === 0 && (
                                <EmptyState icon="bell-ring" title="No mention rules yet" />
                            )}
                            {rules.map((rule, idx) => (
                                <Accordion
                                    key={idx}
                                    title={`Rule #${idx + 1}: ${rule.pattern || '(Empty Pattern)'}`}
                                    defaultOpen={false}
                                    onDelete={() => {
                                        const newRules = rules.filter((_, i) => i !== idx);
                                        updateRuleList('mention_rules', permKey, newRules);
                                    }}
                                >
                                    <div className="space-y-5">
                                        <Input
                                            label="Regex Pattern"
                                            value={rule.pattern}
                                            onChange={(v) => {
                                                const newRules = [...rules];
                                                newRules[idx] = { ...newRules[idx], pattern: v };
                                                updateRuleList('mention_rules', permKey, newRules);
                                            }}
                                            className="font-mono"
                                            placeholder="e.g. @everyone()"
                                        />

                                        <Input
                                            label="Comment (/embellish-chat help mention)"
                                            value={rule.comment || ""}
                                            onChange={(v) => {
                                                const newRules = [...rules];
                                                newRules[idx] = { ...newRules[idx], comment: v };
                                                updateRuleList('mention_rules', permKey, newRules);
                                            }}
                                            placeholder="<blue><b>Pattern</b></blue>: @everyone ..."
                                        />

                                        <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/70 p-4 dark:border-slate-700 dark:bg-slate-900/30">
                                            <div className={`${sectionLabelClass} mb-4`}>Core Settings</div>
                                            <div className="grid grid-cols-1 gap-4 md:grid-cols-12">
                                                <Input
                                                    label="Title (Placeholder API Supported)"
                                                    value={rule.title}
                                                    onChange={(v) => {
                                                        const newRules = [...rules];
                                                        newRules[idx] = { ...newRules[idx], title: v };
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-7"
                                                />
                                                <Input
                                                    label="Cooldown (sec)"
                                                    type="number"
                                                    step="1"
                                                    value={rule.cooldown !== undefined ? rule.cooldown : 0}
                                                    onChange={(v) => {
                                                        const newRules = [...rules];
                                                        newRules[idx] = { ...newRules[idx], cooldown: parseInt(v) || 0 };
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-3"
                                                />
                                                <div className="md:col-span-2 flex items-end">
                                                    <InlineCheckbox
                                                        id={`onlyTarget-${permKey}-${idx}`}
                                                        label="Only Target"
                                                        checked={rule.onlyTarget || false}
                                                        onChange={(checked) => {
                                                            const newRules = [...rules];
                                                            newRules[idx] = { ...newRules[idx], onlyTarget: checked };
                                                            updateRuleList('mention_rules', permKey, newRules);
                                                        }}
                                                    />
                                                </div>
                                            </div>
                                        </div>

                                        <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/80 p-4 dark:border-slate-700 dark:bg-slate-900/30">
                                            <div className={`${sectionLabelClass} mb-4`}>Notification Sound</div>
                                            <div className="grid grid-cols-1 gap-4 md:grid-cols-12">
                                                <Input
                                                    label="Sound ID"
                                                    value={rule.sound?.id}
                                                    onChange={(v) => {
                                                        const newRules = updateSound(rules, idx, 'id', v);
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-5"
                                                    placeholder="minecraft:entity.experience_orb.pickup"
                                                />
                                                <Combobox
                                                    label="Category"
                                                    value={rule.sound?.category}
                                                    options={SOUND_CATEGORIES}
                                                    onChange={(v) => {
                                                        const newRules = updateSound(rules, idx, 'category', v);
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-3"
                                                />
                                                <Input
                                                    label="Volume"
                                                    type="number"
                                                    step="0.1"
                                                    value={rule.sound?.volume}
                                                    onChange={(v) => {
                                                        const newRules = updateSound(rules, idx, 'volume', parseFloat(v));
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-2"
                                                />
                                                <Input
                                                    label="Pitch"
                                                    type="number"
                                                    step="0.1"
                                                    value={rule.sound?.pitch}
                                                    onChange={(v) => {
                                                        const newRules = updateSound(rules, idx, 'pitch', parseFloat(v));
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                    className="md:col-span-2"
                                                />
                                            </div>
                                        </div>

                                        <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
                                            <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/80 p-4 dark:border-slate-700 dark:bg-slate-900/30">
                                                <MentionActionsEditor
                                                    actions={rule.mentions || []}
                                                    onChange={(newActions) => {
                                                        const newRules = [...rules];
                                                        newRules[idx] = { ...newRules[idx], mentions: newActions };
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                />
                                            </div>

                                            <div className="rounded-[24px] border border-slate-200/80 bg-slate-50/80 p-4 dark:border-slate-700 dark:bg-slate-900/30">
                                                <StyleActionsEditor
                                                    actions={rule.styles || []}
                                                    onChange={(newActions) => {
                                                        const newRules = [...rules];
                                                        newRules[idx] = { ...newRules[idx], styles: newActions };
                                                        updateRuleList('mention_rules', permKey, newRules);
                                                    }}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                </Accordion>
                            ))}
                            <Button
                                className="w-full mt-2"
                                variant="secondary"
                                onClick={() => {
                                    const newRules = [...rules, {
                                        pattern: "",
                                        comment: "",
                                        title: "",
                                        cooldown: 0,
                                        onlyTarget: false,
                                        sound: {
                                            id: "minecraft:entity.experience_orb.pickup",
                                            category: "UI",
                                            volume: 1.0,
                                            pitch: 1.0
                                        },
                                        mentions: [],
                                        styles: []
                                    }];
                                    updateRuleList('mention_rules', permKey, newRules);
                                }}
                            >
                                <IconWrapper name="plus" size={16} /> Add Mention Rule
                            </Button>
                        </div>
                    </Card>
                ))}
            </div>
        );
    };

    const JsonView = ({
        configJson, setConfigJson,
        stylesJson, setStylesJson,
        mentionsJson, setMentionsJson,
        presetsJson, setPresetsJson,
        handleImport, copyToClipboard, copySuccess, error, onReset,
        activeJsonTab, setActiveJsonTab
    }) => {
        const jsonTabs = [
            { id: "config", label: "config.json", icon: "settings" },
            { id: "styles", label: "styles.json", icon: "message-square" },
            { id: "mentions", label: "mentions.json", icon: "at-sign" },
            { id: "presets", label: "presets.json", icon: "swatch-book" },
        ];

        const getCurrentJson = () => {
            if (activeJsonTab === "config") return configJson;
            if (activeJsonTab === "styles") return stylesJson;
            if (activeJsonTab === "mentions") return mentionsJson;
            return presetsJson;
        };

        const setCurrentJson = (val) => {
            if (activeJsonTab === "config") setConfigJson(val);
            else if (activeJsonTab === "styles") setStylesJson(val);
            else if (activeJsonTab === "mentions") setMentionsJson(val);
            else setPresetsJson(val);
        };

        const saveCurrentJsonToFile = () => {
            const blob = new Blob([getCurrentJson()], { type: "application/json;charset=utf-8" });
            const url = URL.createObjectURL(blob);
            const anchor = document.createElement("a");
            anchor.href = url;
            anchor.download = `${activeJsonTab}.json`;
            document.body.appendChild(anchor);
            anchor.click();
            document.body.removeChild(anchor);
            URL.revokeObjectURL(url);
        };

        return (
            <div className="space-y-6">
                <Card className="p-6 md:p-8">
                    <div className="flex flex-col gap-5 xl:flex-row xl:items-end xl:justify-between">
                        <SectionHeading
                            eyebrow="Import / Export"
                            title="JSON Console"
                            icon="file-json"
                        />
                        <div className="flex flex-wrap gap-2">
                            <DeleteButton
                                onDelete={onReset}
                                label={<><IconWrapper name="rotate-ccw" size={14} className="mr-1 inline" /> Reset All</>}
                                size="md"
                            />
                            <Button onClick={() => handleImport(activeJsonTab)} variant="secondary">
                                <IconWrapper name="upload" size={16} /> Load from Text
                            </Button>
                            <Button onClick={saveCurrentJsonToFile} variant="secondary">
                                <IconWrapper name="download" size={16} /> Save to File
                            </Button>
                            <Button onClick={() => copyToClipboard(activeJsonTab)} variant="primary">
                                {copySuccess === activeJsonTab ? <IconWrapper name="check" size={16} /> : <IconWrapper name="copy" size={16} />}
                                {copySuccess === activeJsonTab ? "Copied!" : "Copy to Clipboard"}
                            </Button>
                        </div>
                    </div>
                </Card>

                <Card className="p-4 md:p-5">
                    <div className="flex flex-wrap gap-2">
                        {jsonTabs.map(tab => (
                            <TabButton
                                key={tab.id}
                                active={activeJsonTab === tab.id}
                                onClick={() => setActiveJsonTab(tab.id)}
                                icon={tab.icon}
                                label={tab.label}
                                compactLabel={tab.label.replace('.json', '')}
                            />
                        ))}
                    </div>
                </Card>

                {error && (
                    <div className="rounded-[24px] border border-red-200 bg-red-50 px-5 py-4 text-sm font-medium text-red-700 shadow-sm dark:border-red-900/50 dark:bg-red-950/30 dark:text-red-300">
                        {error}
                    </div>
                )}

                <Card className="overflow-hidden">
                    <div className="flex items-center justify-between border-b border-slate-200/80 bg-gradient-to-r from-slate-50 to-white px-6 py-4 dark:border-slate-700 dark:from-slate-900/70 dark:to-slate-900/20">
                        <div>
                            <div className="text-[11px] font-bold uppercase tracking-[0.22em] text-indigo-500">Active File</div>
                            <div className="mt-1 text-sm font-bold text-slate-800 dark:text-white">{activeJsonTab}.json</div>
                        </div>
                    </div>
                    <textarea
                        className="min-h-[620px] w-full resize-y border-0 bg-slate-950 px-6 py-5 font-mono text-sm leading-6 text-slate-100 outline-none focus:ring-4 focus:ring-indigo-500/20"
                        value={getCurrentJson()}
                        onChange={(e) => setCurrentJson(e.target.value)}
                        spellCheck="false"
                    />
                </Card>
            </div>
        );
    };

    function ConfigEditor() {
        const [defaultData, setDefaultData] = useState(null);
        const [config, setConfig] = useState(null);
        const [styles, setStyles] = useState(null);
        const [mentions, setMentions] = useState(null);
        const [presets, setPresets] = useState(null);
        const playerProfileCacheRef = useRef({});

        const [activeTab, setActiveTab] = useState("global");
        const [activeJsonTab, setActiveJsonTab] = useState("config");

        const [configJson, setConfigJson] = useState("");
        const [stylesJson, setStylesJson] = useState("");
        const [mentionsJson, setMentionsJson] = useState("");
        const [presetsJson, setPresetsJson] = useState("");

        const [copySuccess, setCopySuccess] = useState(null);
        const [error, setError] = useState(null);

        useEffect(() => {
            let cancelled = false;

            Promise.all([
                fetchDefaultJson(DEFAULT_FILE_PATHS.config),
                fetchDefaultJson(DEFAULT_FILE_PATHS.styles),
                fetchDefaultJson(DEFAULT_FILE_PATHS.mentions),
                fetchDefaultJson(DEFAULT_FILE_PATHS.presets)
            ])
                .then(([defaultConfig, defaultStyles, defaultMentions, defaultPresets]) => {
                    if (cancelled) return;

                    const nextDefaults = {
                        config: defaultConfig,
                        styles: defaultStyles,
                        mentions: defaultMentions,
                        presets: defaultPresets
                    };

                    setDefaultData(nextDefaults);
                    setConfig(cloneJson(nextDefaults.config));
                    setStyles(cloneJson(nextDefaults.styles));
                    setMentions(cloneJson(nextDefaults.mentions));
                    setPresets(cloneJson(nextDefaults.presets));
                    setError(null);
                })
                .catch((loadError) => {
                    if (cancelled) return;
                    setError("Failed to load default configuration files: " + loadError.message);
                });

            return () => {
                cancelled = true;
            };
        }, []);

        useEffect(() => {
            if (config === null) return;
            setConfigJson(JSON.stringify(config, null, 2));
        }, [config]);

        useEffect(() => {
            if (styles === null) return;
            setStylesJson(JSON.stringify(styles, null, 2));
        }, [styles]);

        useEffect(() => {
            if (mentions === null) return;
            setMentionsJson(JSON.stringify(mentions, null, 2));
        }, [mentions]);

        useEffect(() => {
            if (presets === null) return;
            setPresetsJson(JSON.stringify(presets, null, 2));
        }, [presets]);

        const normalizeConfigData = (rawConfig = {}) => {
            const parsed = { ...rawConfig };

            if (parsed.commandAlias !== undefined && parsed.command_alias === undefined) {
                parsed.command_alias = parsed.commandAlias;
            }
            if (parsed.urlColor !== undefined && parsed.url_color === undefined) {
                parsed.url_color = parsed.urlColor;
            }
            if (parsed.defaultTeamColor !== undefined && parsed.team_color === undefined) {
                parsed.team_color = parsed.defaultTeamColor;
            }
            if (parsed.notificationCommandEnable !== undefined && parsed.notify_command_enabled === undefined) {
                parsed.notify_command_enabled = parsed.notificationCommandEnable;
            }
            if (parsed.mentionBroadcast !== undefined && parsed.notify_mention_enabled === undefined) {
                parsed.notify_mention_enabled = parsed.mentionBroadcast;
            }
            if (parsed.useClearFormat !== undefined && parsed.disable_vanilla_chat_format === undefined) {
                parsed.disable_vanilla_chat_format = parsed.useClearFormat;
            }
            if (parsed.bannedPlayerList !== undefined && parsed.banned_players === undefined) {
                parsed.banned_players = parsed.bannedPlayerList;
            }
            if (parsed.notificationOffPlayerList !== undefined && parsed.notify_off_players === undefined) {
                parsed.notify_off_players = parsed.notificationOffPlayerList;
            }
            if (parsed.mentionColor !== undefined && parsed.team_color === undefined) {
                parsed.team_color = parsed.mentionColor;
            }

            delete parsed.commandAlias;
            delete parsed.urlColor;
            delete parsed.defaultTeamColor;
            delete parsed.notificationCommandEnable;
            delete parsed.mentionBroadcast;
            delete parsed.useClearFormat;
            delete parsed.bannedPlayerList;
            delete parsed.notificationOffPlayerList;
            delete parsed.mentionColor;
            delete parsed.webhook;

            const migratedPresets = {};
            if (parsed.colors !== undefined && parsed.color === undefined) {
                parsed.color = parsed.colors;
            }
            if (parsed.atlas !== undefined) {
                migratedPresets.atlas = parsed.atlas;
                delete parsed.atlas;
            }
            if (parsed.whitelist !== undefined) {
                migratedPresets.whitelist = parsed.whitelist;
                delete parsed.whitelist;
            }
            if (parsed.prefixes !== undefined && parsed.prefix === undefined) {
                parsed.prefix = parsed.prefixes;
            }
            if (parsed.color !== undefined) {
                migratedPresets.color = parsed.color;
                delete parsed.color;
            }
            if (parsed.prefix !== undefined) {
                migratedPresets.prefix = parsed.prefix;
                delete parsed.prefix;
            }
            delete parsed.colors;
            delete parsed.prefixes;

            return {
                config: {
                    ...cloneJson(defaultData?.config || {}),
                    ...parsed,
                    version: CONFIG_VERSION,
                    banned_players: Array.isArray(parsed.banned_players) ? parsed.banned_players : [],
                    notify_off_players: Array.isArray(parsed.notify_off_players) ? parsed.notify_off_players : []
                },
                migratedPresets
            };
        };

        const addUuidEntry = (key, entry, duplicateMessage) => {
            if (!entry) return;
            if (!UUID_PATTERN.test(entry)) {
                alert("Invalid UUID format.");
                return;
            }
            if ((config[key] || []).includes(entry)) {
                alert(duplicateMessage);
                return;
            }
            setConfig(prev => ({
                ...prev,
                [key]: [...(prev[key] || []), entry]
            }));
        };

        const removeUuidEntry = (key, entry) => {
            setConfig(prev => ({
                ...prev,
                [key]: (prev[key] || []).filter((value) => value !== entry)
            }));
        };

        const fetchPlayerProfile = async (uuid) => {
            const normalizedUuid = uuid.replace(/-/g, "");
            const cachedProfile = playerProfileCacheRef.current[normalizedUuid];
            if (cachedProfile) return cachedProfile;

            const profileApis = [
                {
                    label: "PlayerDB",
                    url: `https://playerdb.co/api/player/minecraft/${normalizedUuid}`,
                    pickName: (json) => json?.data?.player?.username || json?.data?.player?.raw_id
                },
                {
                    label: "Ashcon",
                    url: `https://api.ashcon.app/mojang/v2/user/${normalizedUuid}`,
                    pickName: (json) => json?.username || json?.name
                },
                {
                    label: "Mojang Session Server",
                    url: `https://sessionserver.mojang.com/session/minecraft/profile/${normalizedUuid}`,
                    pickName: (json) => json?.name
                }
            ];

            let resolvedName = null;
            const failures = [];

            for (const api of profileApis) {
                try {
                    const response = await fetch(api.url, {
                        method: "GET",
                        headers: { "Accept": "application/json" }
                    });

                    if (!response.ok) {
                        failures.push(`${api.label}: HTTP ${response.status}`);
                        continue;
                    }

                    const profileJson = await response.json();
                    const candidateName = api.pickName(profileJson);
                    if (candidateName) {
                        resolvedName = candidateName;
                        break;
                    }

                    failures.push(`${api.label}: empty response`);
                } catch (error) {
                    failures.push(`${api.label}: ${error?.message || "request failed"}`);
                }
            }

            if (!resolvedName) {
                throw new Error(`Could not resolve this UUID. ${failures.join(" | ")}`);
            }

            const profile = {
                uuid,
                normalizedUuid,
                name: resolvedName,
                headUrl: `https://mc-heads.net/head/${normalizedUuid}/96`,
                avatarUrl: `https://mc-heads.net/avatar/${normalizedUuid}/64`
            };

            playerProfileCacheRef.current[normalizedUuid] = profile;
            return profile;
        };

        const handleImport = (fileType) => {
            try {
                if (fileType === "config") {
                    const parsed = JSON.parse(configJson);
                    const { config: normalizedConfig, migratedPresets } = normalizeConfigData(parsed);

                    if (Object.keys(migratedPresets).length > 0) {
                        setPresets(prev => ({
                            ...prev,
                            ...migratedPresets,
                        }));
                    }
                    setConfig(normalizedConfig);
                } else if (fileType === "styles") {
                    const parsed = JSON.parse(stylesJson);
                    if (parsed.stylingRules && parsed.style_rules === undefined) {
                        parsed.style_rules = parsed.stylingRules;
                    }
                    delete parsed.stylingRules;
                    if (parsed.style_rules) {
                        Object.keys(parsed.style_rules).forEach(key => {
                            parsed.style_rules[key].forEach(rule => {
                                if (rule.comment === undefined) rule.comment = "";
                            });
                        });
                    }
                    setStyles({
                        ...cloneJson(defaultData.styles),
                        ...parsed,
                        style_rules: parsed.style_rules ?? {}
                    });
                } else if (fileType === "mentions") {
                    const parsed = JSON.parse(mentionsJson);
                    if (parsed.mentionRules && parsed.mention_rules === undefined) {
                        parsed.mention_rules = parsed.mentionRules;
                    }
                    delete parsed.mentionRules;
                    if (parsed.mention_rules) {
                        Object.keys(parsed.mention_rules).forEach(key => {
                            parsed.mention_rules[key] = parsed.mention_rules[key].map(rule => {
                                if (rule.sound && typeof rule.sound === 'string') {
                                    rule.sound = { id: rule.sound, category: "UI", volume: 1.0, pitch: rule.pitch || 1.0 };
                                    delete rule.pitch;
                                }
                                // Reorder keys: pattern, comment, title, cooldown, onlyTarget, sound, mentions, styles
                                return {
                                    pattern: rule.pattern ?? "",
                                    comment: rule.comment ?? "",
                                    title: rule.title ?? "",
                                    cooldown: rule.cooldown ?? 0,
                                    onlyTarget: rule.onlyTarget ?? false,
                                    sound: rule.sound ?? { id: "minecraft:entity.experience_orb.pickup", category: "UI", volume: 1.0, pitch: 1.0 },
                                    mentions: rule.mentions ?? [],
                                    styles: rule.styles ?? []
                                };
                            });
                        });
                    }
                    setMentions({
                        ...cloneJson(defaultData.mentions),
                        ...parsed,
                        mention_rules: parsed.mention_rules ?? {}
                    });
                } else if (fileType === "presets") {
                    const parsed = JSON.parse(presetsJson);
                    if (parsed.colorPreset && parsed.color === undefined && parsed.colors === undefined) {
                        parsed.color = parsed.colorPreset;
                        delete parsed.colorPreset;
                    }
                    if (parsed.atlasPreset && parsed.atlas === undefined) {
                        parsed.atlas = parsed.atlasPreset;
                        delete parsed.atlasPreset;
                    }
                    if (parsed.colors !== undefined && parsed.color === undefined) {
                        parsed.color = parsed.colors;
                    }
                    if (parsed.prefixes !== undefined && parsed.prefix === undefined) {
                        parsed.prefix = parsed.prefixes;
                    }
                    delete parsed.colors;
                    delete parsed.prefixes;
                    if (parsed.color === undefined) parsed.color = {};
                    if (parsed.atlas === undefined) parsed.atlas = {};
                    if (parsed.whitelist === undefined) parsed.whitelist = [];
                    if (parsed.prefix === undefined) parsed.prefix = {};
                    setPresets(parsed);
                }
                setError(null);
                alert(`${fileType}.json loaded successfully!`);
            } catch (e) {
                setError("Invalid JSON: " + e.message);
            }
        };

        const handleReset = () => {
            setConfig(cloneJson(defaultData.config));
            setStyles(cloneJson(defaultData.styles));
            setMentions(cloneJson(defaultData.mentions));
            setPresets(cloneJson(defaultData.presets));
            alert("All configurations reset to defaults.");
        };

        const copyToClipboard = (fileType) => {
            let content;
            if (fileType === "config") content = JSON.stringify(config, null, 2);
            else if (fileType === "styles") content = JSON.stringify(styles, null, 2);
            else if (fileType === "mentions") content = JSON.stringify(mentions, null, 2);
            else content = JSON.stringify(presets, null, 2);

            navigator.clipboard.writeText(content);
            setCopySuccess(fileType);
            setTimeout(() => setCopySuccess(null), 2000);
        };

        const updateGlobal = (key, val) => setConfig(prev => ({ ...prev, [key]: val }));

        const addPreset = (name) => {
            if (!name) return;
            if (presets.color && presets.color[name]) {
                alert("Preset already exists!");
                return;
            }
            setPresets(prev => ({
                ...prev,
                color: { ...(prev.color || {}), [name]: "#FFFFFF" }
            }));
        };

        const removePreset = (key) => {
            const newPresets = { ...presets.color };
            delete newPresets[key];
            setPresets(prev => ({ ...prev, color: newPresets }));
        };

        const updatePreset = (key, val) => {
            setPresets(prev => ({
                ...prev,
                color: { ...prev.color, [key]: val }
            }));
        };

        const addAtlasPreset = (name) => {
            if (!name) return;
            if (presets.atlas && presets.atlas[name]) {
                alert("Atlas preset already exists!");
                return;
            }
            setPresets(prev => ({
                ...prev,
                atlas: { ...(prev.atlas || {}), [name]: { atlas: "minecraft:gui", sprite: "" } }
            }));
        };

        const removeAtlasPreset = (key) => {
            const newPresets = { ...presets.atlas };
            delete newPresets[key];
            setPresets(prev => ({ ...prev, atlas: newPresets }));
        };

        const updateAtlasPreset = (key, field, val) => {
            setPresets(prev => ({
                ...prev,
                atlas: {
                    ...prev.atlas,
                    [key]: { ...prev.atlas[key], [field]: val }
                }
            }));
        };

        const addWhitelist = (entry) => {
            if (!entry) return;
            if ((presets.whitelist || []).includes(entry)) {
                alert("Whitelist entry already exists!");
                return;
            }
            setPresets(prev => ({ ...prev, whitelist: [...(prev.whitelist || []), entry] }));
        };

        const removeWhitelist = (entry) => {
            setPresets(prev => ({
                ...prev,
                whitelist: (prev.whitelist || []).filter((v) => v !== entry)
            }));
        };

        const addPrefix = (key) => {
            if (!key) return;
            if (presets.prefix && presets.prefix[key] !== undefined) {
                alert("Prefix already exists!");
                return;
            }
            setPresets(prev => ({
                ...prev,
                prefix: { ...(prev.prefix || {}), [key]: "" }
            }));
        };

        const removePrefix = (key) => {
            const newPrefixes = { ...(presets.prefix || {}) };
            delete newPrefixes[key];
            setPresets(prev => ({ ...prev, prefix: newPrefixes }));
        };

        const updatePrefix = (key, val) => {
            setPresets(prev => ({
                ...prev,
                prefix: { ...(prev.prefix || {}), [key]: val }
            }));
        };

        const addBannedPlayer = (entry) => addUuidEntry('banned_players', entry, "Banned player UUID already exists!");
        const removeBannedPlayer = (entry) => removeUuidEntry('banned_players', entry);
        const addNotificationOffPlayer = (entry) => addUuidEntry('notify_off_players', entry, "Notification-off player UUID already exists!");
        const removeNotificationOffPlayer = (entry) => removeUuidEntry('notify_off_players', entry);

        const updateStyleRuleList = (permissionKey, newRules) => {
            setStyles(prev => ({
                ...prev,
                style_rules: {
                    ...prev.style_rules,
                    [permissionKey]: newRules
                }
            }));
        };

        const addStylePermissionGroup = (key) => {
            if (!key) return;
            if (styles.style_rules[key]) {
                alert("This permission group already exists!");
                return;
            }
            setStyles(prev => ({
                ...prev,
                style_rules: { ...prev.style_rules, [key]: [] }
            }));
        };

        const removeStylePermissionGroup = (key) => {
            setStyles(prev => {
                const newRules = { ...prev.style_rules };
                delete newRules[key];
                return { ...prev, style_rules: newRules };
            });
        };

        const normalizeMentionRule = (rule) => ({
            pattern: rule.pattern ?? "",
            comment: rule.comment ?? "",
            title: rule.title ?? "",
            cooldown: rule.cooldown ?? 0,
            onlyTarget: rule.onlyTarget ?? false,
            sound: rule.sound ?? { id: "minecraft:entity.experience_orb.pickup", category: "UI", volume: 1.0, pitch: 1.0 },
            mentions: rule.mentions ?? [],
            styles: rule.styles ?? []
        });

        const updateMentionRuleList = (permissionKey, newRules) => {
            setMentions(prev => ({
                ...prev,
                mention_rules: {
                    ...prev.mention_rules,
                    [permissionKey]: newRules.map(normalizeMentionRule)
                }
            }));
        };

        const addMentionPermissionGroup = (key) => {
            if (!key) return;
            if (mentions.mention_rules[key]) {
                alert("This permission group already exists!");
                return;
            }
            setMentions(prev => ({
                ...prev,
                mention_rules: { ...prev.mention_rules, [key]: [] }
            }));
        };

        const removeMentionPermissionGroup = (key) => {
            setMentions(prev => {
                const newRules = { ...prev.mention_rules };
                delete newRules[key];
                return { ...prev, mention_rules: newRules };
            });
        };

        const addPermissionGroup = (category, key) => {
            if (category === 'style_rules') addStylePermissionGroup(key);
            else if (category === 'mention_rules') addMentionPermissionGroup(key);
        };

        const removePermissionGroup = (category, key) => {
            if (category === 'style_rules') removeStylePermissionGroup(key);
            else if (category === 'mention_rules') removeMentionPermissionGroup(key);
        };

        const updateRuleList = (category, permissionKey, newRules) => {
            if (category === 'style_rules') updateStyleRuleList(permissionKey, newRules);
            else if (category === 'mention_rules') updateMentionRuleList(permissionKey, newRules);
        };
        const tabs = [
            { id: "global", label: "Global Settings", compactLabel: "Global", icon: "settings" },
            { id: "styling", label: "Styling Rules", compactLabel: "Styling", icon: "message-square" },
            { id: "mention", label: "Mention Rules", compactLabel: "Mention", icon: "at-sign" },
            { id: "json", label: "Import / Export", compactLabel: "JSON", icon: "file-json" },
        ];

        if (!defaultData || !config || !styles || !mentions || !presets) {
            return (
                <div className="relative min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_right,_rgba(99,102,241,0.16),_transparent_24%),radial-gradient(circle_at_top_left,_rgba(244,63,94,0.10),_transparent_18%),linear-gradient(180deg,_#f8fafc_0%,_#eef2ff_100%)] font-sans text-slate-900">
                    <div className="relative mx-auto flex min-h-screen max-w-4xl items-center justify-center px-4 py-8">
                        <Card className="w-full max-w-xl p-8">
                            <div className="space-y-3 text-center">
                                <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-[2rem] border border-white/70 bg-white/90 shadow-glow">
                                    <BrandMark size={60} />
                                </div>
                                <h1 className="text-2xl font-black tracking-tight text-slate-900">Config Generator</h1>
                                <p className="text-sm text-slate-600">
                                    {error || "Loading default configuration files..."}
                                </p>
                            </div>
                        </Card>
                    </div>
                </div>
            );
        }

        const stylingRuleCount = Object.values(styles.style_rules || {}).reduce((sum, rules) => sum + rules.length, 0);
        const mentionRuleCount = Object.values(mentions.mention_rules || {}).reduce((sum, rules) => sum + rules.length, 0);
        const presetCount = Object.keys(presets.color || {}).length + Object.keys(presets.atlas || {}).length;
        const activeTabMeta = tabs.find(tab => tab.id === activeTab);

        return (
            <div className="relative min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_right,_rgba(99,102,241,0.16),_transparent_24%),radial-gradient(circle_at_top_left,_rgba(244,63,94,0.10),_transparent_18%),linear-gradient(180deg,_#f8fafc_0%,_#eef2ff_100%)] font-sans text-slate-900">
                <div className="pointer-events-none absolute -left-20 top-20 h-72 w-72 rounded-full bg-indigo-200/40 blur-3xl" />
                <div className="pointer-events-none absolute -right-20 top-40 h-80 w-80 rounded-full bg-fuchsia-200/30 blur-3xl" />

                <div className="relative mx-auto max-w-[92rem] px-4 py-8 md:px-6 md:py-10">
                    <header className="mb-8 space-y-6">
                        <Card className="overflow-visible p-6 md:p-8">
                            <div className="absolute right-0 top-0 h-52 w-52 rounded-full bg-gradient-to-br from-indigo-100 via-purple-100 to-pink-100 blur-3xl opacity-80" />
                            <div className="relative flex flex-col gap-6">
                                <div className="flex flex-col gap-6 md:flex-row md:items-start">
                                    <div className="flex flex-col gap-6 md:flex-row md:items-center">
                                        <div className="flex h-24 w-24 items-center justify-center rounded-[2rem] border border-white/70 bg-white/90 shadow-glow">
                                            <BrandMark size={72} />
                                        </div>
                                        <div>
                                            <p className="text-xs font-semibold uppercase tracking-[0.26em] text-indigo-500">Embellish Chat Toolkit</p>
                                            <h1 className="mt-2 inline-block pb-2 text-4xl font-black leading-[1.2] tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 via-violet-600 to-fuchsia-500 md:text-5xl">
                                                config generator
                                            </h1>
                                            <div className="mt-3 inline-flex items-center rounded-full bg-white/80 px-3 py-1 text-xs font-bold text-slate-600 shadow-sm">
                                                {activeTabMeta?.label}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
                                    <StatPill label="Version" value={config.version || CONFIG_VERSION} accent="indigo" />
                                    <StatPill label="Style Rules" value={stylingRuleCount} accent="emerald" />
                                    <StatPill label="Mention Rules" value={mentionRuleCount} accent="amber" />
                                    <StatPill label="Presets" value={presetCount} accent="slate" />
                                </div>
                            </div>
                        </Card>

                        <Card className="p-3 md:p-4">
                            <div className="flex flex-wrap gap-2">
                                {tabs.map(tab => (
                                    <TabButton
                                        key={tab.id}
                                        active={activeTab === tab.id}
                                        onClick={() => setActiveTab(tab.id)}
                                        icon={tab.icon}
                                        label={tab.label}
                                        compactLabel={tab.compactLabel}
                                    />
                                ))}
                            </div>
                        </Card>
                    </header>

                    <main className="space-y-6 pb-20">
                        {activeTab === 'global' && (
                            <GlobalView
                                config={config}
                                presets={presets}
                                updateGlobal={updateGlobal}
                                addPreset={addPreset}
                                removePreset={removePreset}
                                updatePreset={updatePreset}
                                addAtlasPreset={addAtlasPreset}
                                removeAtlasPreset={removeAtlasPreset}
                                updateAtlasPreset={updateAtlasPreset}
                                addWhitelist={addWhitelist}
                                removeWhitelist={removeWhitelist}
                                addPrefix={addPrefix}
                                removePrefix={removePrefix}
                                updatePrefix={updatePrefix}
                                addBannedPlayer={addBannedPlayer}
                                removeBannedPlayer={removeBannedPlayer}
                                addNotificationOffPlayer={addNotificationOffPlayer}
                                removeNotificationOffPlayer={removeNotificationOffPlayer}
                                fetchPlayerProfile={fetchPlayerProfile}
                            />
                        )}
                        {activeTab === 'styling' && (
                            <StylingView
                                config={styles}
                                addPermissionGroup={(cat, key) => addPermissionGroup('style_rules', key)}
                                removePermissionGroup={(cat, key) => removePermissionGroup('style_rules', key)}
                                updateRuleList={(cat, permKey, rules) => updateRuleList('style_rules', permKey, rules)}
                            />
                        )}
                        {activeTab === 'mention' && (
                            <MentionView
                                config={mentions}
                                addPermissionGroup={(cat, key) => addPermissionGroup('mention_rules', key)}
                                removePermissionGroup={(cat, key) => removePermissionGroup('mention_rules', key)}
                                updateRuleList={(cat, permKey, rules) => updateRuleList('mention_rules', permKey, rules)}
                            />
                        )}
                        {activeTab === 'json' && (
                            <JsonView
                                configJson={configJson}
                                setConfigJson={setConfigJson}
                                stylesJson={stylesJson}
                                setStylesJson={setStylesJson}
                                mentionsJson={mentionsJson}
                                setMentionsJson={setMentionsJson}
                                presetsJson={presetsJson}
                                setPresetsJson={setPresetsJson}
                                handleImport={handleImport}
                                copyToClipboard={copyToClipboard}
                                copySuccess={copySuccess}
                                error={error}
                                onReset={handleReset}
                                activeJsonTab={activeJsonTab}
                                setActiveJsonTab={setActiveJsonTab}
                            />
                        )}
                    </main>

                </div>
            </div>
        );
    }

    const root = ReactDOM.createRoot(document.getElementById('root'));
    root.render(<ConfigEditor />);
