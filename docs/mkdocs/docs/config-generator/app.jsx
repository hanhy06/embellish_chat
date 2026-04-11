const { useEffect, useMemo, useRef, useState } = React;

const FILES = {
  config: "defaults/config.json",
  styles: "defaults/styles.json",
  mentions: "defaults/mentions.json",
  presets: "defaults/presets.json"
};

const STYLE_TYPES = [
  "COLOR_HEX", "COLOR_RAINBOW", "COLOR_GRADIENT", "COLOR_PRESET", "COLOR_SHADOW", "COLOR_TEAM",
  "BOLD", "ITALIC", "UNDERLINE", "STRIKETHROUGH", "OBFUSCATED", "FONT", "CLEAR",
  "CLICK_COMMAND_RUN", "CLICK_COMMAND_SUGGEST", "CLICK_COPY", "HOVER_TEXT", "HOVER_ITEM", "URL", "METADATA",
  "UPPER", "LOWER", "CAPITALIZE", "REPLACE", "MASK", "PREFIX", "SUFFIX",
  "SHOW_ITEM", "SHOW_INVENTORY", "SHOW_ENDER_CHEST", "ICON_PRESET", "JSON", "DISCORD_JSON", "COMMAND_RUN", "LOG", "BUBBLE", "BLOCK"
];

const MENTION_TYPES = [
  "PLAYER",
  "TEAM",
  "INSIDE",
  "EVERYONE",
  "WORLD",
  "LUCK_PERMS_GROUP",
  "PERMISSION",
  "CUSTOM"
];

const SOUND_CATEGORIES = [
  "MASTER", "MUSIC", "RECORDS", "WEATHER", "BLOCKS", "HOSTILE", "NEUTRAL", "PLAYERS", "AMBIENT", "VOICE", "UI"
];

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
const clone = (value) => JSON.parse(JSON.stringify(value));
const fetchJson = async (path) => {
  const response = await fetch(path, { cache: "no-store" });
  if (!response.ok) throw new Error(`Failed to load ${path}`);
  return response.json();
};

const emptyStyle = () => ({ styleType: "BOLD", preset: "" });
const emptyMention = () => ({ mentionType: "PLAYER", preset: "" });
const emptySound = () => ({
  id: "minecraft:entity.experience_orb.pickup",
  category: "UI",
  volume: 1,
  pitch: 1
});
const emptyStyleRule = () => ({ pattern: "", comment: "", styles: [emptyStyle()] });
const emptyMentionRule = () => ({
  pattern: "",
  comment: "",
  title: "%player:displayname% mentioned you",
  cooldown: 0,
  onlyTarget: false,
  sound: emptySound(),
  mentions: [emptyMention()],
  styles: [emptyStyle()]
});

const normalizeConfig = (value) => ({
  version: value.version ?? "3.5.1",
  delimiter: value.delimiter ?? ",",
  timestamp: value.timestamp ?? "yyyy-MM-dd HH:mm:ss",
  command_alias: value.command_alias ?? "ec",
  url_color: value.url_color ?? "#0000EE",
  team_color: value.team_color ?? "#FF55FF",
  notify_command_enabled: value.notify_command_enabled ?? true,
  notify_mention_enabled: value.notify_mention_enabled ?? true,
  disable_vanilla_chat_format: value.disable_vanilla_chat_format ?? false,
  banned_players: value.banned_players ?? [],
  notify_off_players: value.notify_off_players ?? []
});

const normalizePresets = (value) => ({
  prefix: value.prefix ?? value.prefixes ?? {},
  whitelist: value.whitelist ?? [],
  icon: value.icon ?? {},
  item: value.item ?? {},
  color: value.color ?? value.colors ?? {}
});

const normalizeStyleRule = (rule) => ({
  pattern: rule.pattern ?? "",
  comment: rule.comment ?? "",
  styles: (rule.styles ?? []).map((style) => ({
    styleType: style.styleType ?? "BOLD",
    preset: style.preset ?? ""
  }))
});

const normalizeStyles = (value) => ({
  style_rules: Object.fromEntries(
    Object.entries(value.style_rules ?? {}).map(([key, rules]) => [key, (rules ?? []).map(normalizeStyleRule)])
  )
});

const normalizeMentionRule = (rule) => {
  const sound = typeof rule.sound === "string"
    ? { id: rule.sound, category: "UI", volume: 1, pitch: rule.pitch ?? 1 }
    : rule.sound;

  return {
    pattern: rule.pattern ?? "",
    comment: rule.comment ?? "",
    title: rule.title ?? "%player:displayname% mentioned you",
    cooldown: rule.cooldown ?? 0,
    onlyTarget: rule.onlyTarget ?? false,
    sound: {
      ...emptySound(),
      ...(sound ?? {})
    },
    mentions: (rule.mentions ?? []).map((mention) => ({
      mentionType: mention.mentionType ?? "PLAYER",
      preset: mention.preset ?? ""
    })),
    styles: (rule.styles ?? []).map((style) => ({
      styleType: style.styleType ?? "BOLD",
      preset: style.preset ?? ""
    }))
  };
};

const normalizeMentions = (value) => ({
  mention_rules: Object.fromEntries(
    Object.entries(value.mention_rules ?? {}).map(([key, rules]) => [key, (rules ?? []).map(normalizeMentionRule)])
  )
});

const stringify = (value) => JSON.stringify(value, null, 2);
const kebabDownload = (name, content) => {
  const blob = new Blob([content], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = `${name}.json`;
  anchor.click();
  URL.revokeObjectURL(url);
};

const regexState = (pattern) => {
  if (!pattern) return null;
  try {
    new RegExp(pattern);
    return { ok: true, text: "Valid regex" };
  } catch (error) {
    return { ok: false, text: error.message };
  }
};

const shellClass = "rounded-xl border border-neutral-800 bg-neutral-950";
const inputClass = "w-full rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2 text-sm text-neutral-100 outline-none transition focus:border-neutral-600";
const buttonClass = "rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2 text-sm font-medium text-neutral-100 transition hover:border-neutral-600 hover:bg-neutral-900";
const escapeRawText = (value) => (value ?? "").replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t");
const unescapeRawText = (value) => {
  let output = "";

  for (let index = 0; index < value.length; index += 1) {
    const current = value[index];
    const next = value[index + 1];

    if (current !== "\\") {
      output += current;
      continue;
    }

    if (next === "n") {
      output += "\n";
      index += 1;
      continue;
    }

    if (next === "r") {
      output += "\r";
      index += 1;
      continue;
    }

    if (next === "t") {
      output += "\t";
      index += 1;
      continue;
    }

    if (next === "\\") {
      output += "\\";
      index += 1;
      continue;
    }

    output += "\\";
  }

  return output;
};

function Button({ children, className = "", ...props }) {
  return <button type="button" className={`${buttonClass} ${className}`} {...props}>{children}</button>;
}

function Field({ label, children }) {
  return (
    <label className="flex flex-col gap-2">
      <span className="text-xs font-medium uppercase tracking-wide text-neutral-400">{label}</span>
      {children}
    </label>
  );
}

function Input({ className = "", ...props }) {
  return <input className={`${inputClass} ${className}`} {...props} />;
}

function TextArea({ className = "", ...props }) {
  return <textarea className={`${inputClass} min-h-24 resize-y ${className}`} {...props} />;
}

function RawTextArea({ value, onValueChange, ...props }) {
  return (
    <TextArea
      className="font-mono text-[13px] leading-6"
      spellCheck={false}
      autoCapitalize="off"
      autoCorrect="off"
      wrap="off"
      value={escapeRawText(value)}
      onChange={(event) => onValueChange(unescapeRawText(event.target.value))}
      {...props}
    />
  );
}

function Select({ options, className = "", ...props }) {
  return (
    <select className={`${inputClass} ${className}`} {...props}>
      {options.map((option) => <option key={option} value={option}>{option}</option>)}
    </select>
  );
}

function Toggle({ label, checked, onChange }) {
  return (
    <label className="flex items-center justify-between gap-3 rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2 text-sm text-neutral-100">
      <span>{label}</span>
      <input type="checkbox" checked={checked} onChange={(event) => onChange(event.target.checked)} />
    </label>
  );
}

function Section({ title, description, actions, children }) {
  return (
    <section className={`${shellClass} p-4 md:p-5`}>
      <div className="mb-4 flex flex-col gap-3 xl:flex-row xl:items-start xl:justify-between">
        <div className="min-w-0 flex-1">
          <h2 className="text-lg font-semibold text-white">{title}</h2>
          {description && <p className="mt-1 text-sm text-neutral-400">{description}</p>}
        </div>
        {actions && <div className="flex w-full items-start justify-start xl:w-auto xl:flex-none xl:justify-end">{actions}</div>}
      </div>
      <div className="space-y-4">{children}</div>
    </section>
  );
}

function Subsection({ title, description, actions, children }) {
  return (
    <div className="rounded-lg border border-neutral-800 bg-black/30 p-3">
      <div className="mb-3 flex flex-col gap-2 md:flex-row md:items-start md:justify-between">
        <div>
          <div className="text-xs font-medium uppercase tracking-wide text-neutral-400">{title}</div>
          {description && <div className="mt-1 text-sm text-neutral-500">{description}</div>}
        </div>
        {actions && <div className="flex justify-end">{actions}</div>}
      </div>
      {children}
    </div>
  );
}

function ColorInput({ value, onChange, placeholder = "#RRGGBB" }) {
  return (
    <div className="flex items-center gap-3 rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2">
      <input
        type="color"
        value={value || "#000000"}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 w-12 shrink-0 cursor-pointer rounded-md border border-neutral-700 bg-transparent p-1"
      />
      <input
        type="text"
        value={value || ""}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        className="w-full border-0 bg-transparent px-0 py-0 text-sm text-neutral-100 outline-none"
        style={{ color: value || undefined }}
      />
    </div>
  );
}

function ListTextEditor({ title, values, onAdd, onChange, onRemove, placeholder }) {
  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-medium text-white">{title}</div>
        <Button onClick={() => onAdd("")}>Add</Button>
      </div>
      {values.length === 0 && <div className="text-sm text-neutral-500">Empty</div>}
      <div className="space-y-2">
        {values.map((value, index) => (
          <div key={index} className="flex gap-2">
            <Input value={value} placeholder={placeholder} onChange={(event) => onChange(index, event.target.value)} />
            <Button onClick={() => onRemove(index)}>Remove</Button>
          </div>
        ))}
      </div>
    </div>
  );
}

function ColorPresetEditor({ values, onAdd, onRename, onChange, onRemove }) {
  const entries = Object.entries(values);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-medium text-white">Color Presets</div>
        <Button onClick={onAdd}>Add</Button>
      </div>
      {entries.length === 0 && <div className="text-sm text-neutral-500">Empty</div>}
      <div className="grid gap-3 xl:grid-cols-2">
        {entries.map(([key, value]) => (
          <div key={key} className="rounded-lg border border-neutral-800 bg-neutral-950 p-3">
            <div className="grid gap-3 md:grid-cols-[auto_1fr_160px_auto]">
              <input
                type="color"
                value={value || "#000000"}
                onChange={(event) => onChange(key, event.target.value)}
                className="h-11 w-14 cursor-pointer rounded-lg border border-neutral-700 bg-transparent p-1"
              />
              <Input
                value={key}
                placeholder="preset name"
                onChange={(event) => onRename(key, event.target.value)}
                style={{
                  color: value || undefined,
                  WebkitTextStroke: "0.2px rgba(255,255,255,0.8)",
                  textShadow: "0 0 1px rgba(255,255,255,0.45)"
                }}
              />
              <Input
                value={value}
                placeholder="#RRGGBB"
                onChange={(event) => onChange(key, event.target.value)}
                className="font-mono"
              />
              <Button onClick={() => onRemove(key)}>Remove</Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function KeyValueEditor({ title, values, onAdd, onRename, onChange, onRemove, keyPlaceholder, valuePlaceholder }) {
  const entries = Object.entries(values);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-medium text-white">{title}</div>
        <Button onClick={onAdd}>Add</Button>
      </div>
      {entries.length === 0 && <div className="text-sm text-neutral-500">Empty</div>}
      <div className="space-y-2">
        {entries.map(([key, value]) => (
          <div key={key} className="grid gap-2 md:grid-cols-[1fr_1fr_auto]">
            <Input value={key} placeholder={keyPlaceholder} onChange={(event) => onRename(key, event.target.value)} />
            <Input value={value} placeholder={valuePlaceholder} onChange={(event) => onChange(key, event.target.value)} />
            <Button onClick={() => onRemove(key)}>Remove</Button>
          </div>
        ))}
      </div>
    </div>
  );
}

function AtlasEditor({ title, values, onAdd, onRename, onChange, onRemove }) {
  const entries = Object.entries(values);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-medium text-white">{title}</div>
        <Button onClick={onAdd}>Add</Button>
      </div>
      {entries.length === 0 && <div className="text-sm text-neutral-500">Empty</div>}
      <div className="space-y-3">
        {entries.map(([key, value]) => (
          <div key={key} className="rounded-lg border border-neutral-800 bg-neutral-950 p-3">
            <div className="grid gap-2 md:grid-cols-[1fr_1fr_1fr_auto]">
              <Input value={key} placeholder="preset name" onChange={(event) => onRename(key, event.target.value)} />
              <Input value={value.atlas ?? ""} placeholder="atlas" onChange={(event) => onChange(key, "atlas", event.target.value)} />
              <Input value={value.sprite ?? ""} placeholder="sprite" onChange={(event) => onChange(key, "sprite", event.target.value)} />
              <Button onClick={() => onRemove(key)}>Remove</Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function PlayerListEditor({ title, values, onAdd, onChange, onRemove, placeholder, fetchPlayerProfile }) {
  const [expanded, setExpanded] = useState(null);
  const [previewState, setPreviewState] = useState({ status: "idle", data: null, error: "" });

  const togglePreview = async (value) => {
    if (expanded === value) {
      setExpanded(null);
      setPreviewState({ status: "idle", data: null, error: "" });
      return;
    }

    setExpanded(value);

    if (!UUID_PATTERN.test(value)) {
      setPreviewState({ status: "error", data: null, error: "Invalid UUID format." });
      return;
    }

    setPreviewState({ status: "loading", data: null, error: "" });

    try {
      const data = await fetchPlayerProfile(value);
      setPreviewState({ status: "success", data, error: "" });
    } catch (error) {
      const normalizedUuid = value.replace(/-/g, "");
      setPreviewState({
        status: "error",
        data: {
          uuid: value,
          normalizedUuid,
          name: "Unknown Player",
          headUrl: `https://mc-heads.net/head/${normalizedUuid}/96`
        },
        error: error.message || "Could not load player profile."
      });
    }
  };

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-medium text-white">{title}</div>
        <Button onClick={() => onAdd("")}>Add</Button>
      </div>
      {values.length === 0 && <div className="text-sm text-neutral-500">Empty</div>}
      <div className="space-y-2">
        {values.map((value, index) => (
          <div key={`${value}-${index}`} className="rounded-lg border border-neutral-800 bg-neutral-950 p-3">
            <div className="flex flex-col gap-2 md:flex-row">
              <Input value={value} placeholder={placeholder} onChange={(event) => onChange(index, event.target.value)} />
              <Button onClick={() => togglePreview(value)}>Preview</Button>
              <Button onClick={() => onRemove(index)}>Remove</Button>
            </div>
            {expanded === value && (
              <div className="mt-3 rounded-lg border border-neutral-800 bg-black/30 p-3">
                {previewState.status === "loading" && <div className="text-sm text-neutral-400">Loading player profile...</div>}
                {previewState.status !== "loading" && previewState.data && (
                  <div className="flex flex-col gap-3 md:flex-row md:items-center">
                    <img src={previewState.data.headUrl} alt={`${previewState.data.name} head`} className="h-20 w-20 rounded-lg border border-neutral-800 object-cover" />
                    <div className="min-w-0 flex-1">
                      <div className="text-xs uppercase tracking-wide text-neutral-500">Player Name</div>
                      <div className="truncate text-lg font-semibold text-white">{previewState.data.name}</div>
                      <div className="mt-2 text-xs uppercase tracking-wide text-neutral-500">UUID</div>
                      <div className="break-all font-mono text-sm text-neutral-300">{previewState.data.uuid}</div>
                      {previewState.error && <div className="mt-3 text-sm text-amber-300">{previewState.error}</div>}
                    </div>
                  </div>
                )}
                {previewState.status === "error" && !previewState.data && <div className="text-sm text-amber-300">{previewState.error}</div>}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

function StyleListEditor({ items, onChange }) {
  const update = (index, patch) => onChange(items.map((item, current) => current === index ? { ...item, ...patch } : item));
  const remove = (index) => onChange(items.filter((_, current) => current !== index));

  return (
    <div className="space-y-2">
      {items.map((item, index) => (
        <div key={index} className="grid gap-2 rounded-lg border border-neutral-800 bg-neutral-950 p-3 md:grid-cols-[1fr_2fr_auto]">
          <Select options={STYLE_TYPES} value={item.styleType} onChange={(event) => update(index, { styleType: event.target.value })} />
          <Input value={item.preset} placeholder="preset" onChange={(event) => update(index, { preset: event.target.value })} />
          <Button onClick={() => remove(index)}>Remove</Button>
        </div>
      ))}
    </div>
  );
}

function MentionTargetEditor({ items, onChange }) {
  const update = (index, patch) => onChange(items.map((item, current) => current === index ? { ...item, ...patch } : item));
  const remove = (index) => onChange(items.filter((_, current) => current !== index));

  return (
    <div className="space-y-2">
      {items.map((item, index) => (
        <div key={index} className="grid gap-2 rounded-lg border border-neutral-800 bg-neutral-950 p-3 md:grid-cols-[1fr_2fr_auto]">
          <Select options={MENTION_TYPES} value={item.mentionType} onChange={(event) => update(index, { mentionType: event.target.value })} />
          <Input value={item.preset} placeholder="preset / option" onChange={(event) => update(index, { preset: event.target.value })} />
          <Button onClick={() => remove(index)}>Remove</Button>
        </div>
      ))}
    </div>
  );
}

function StyleRuleCard({ rule, onChange, onRemove, index }) {
  const state = regexState(rule.pattern);

  return (
    <div className="space-y-3 rounded-xl border border-neutral-700 bg-neutral-900/80 p-4 shadow-[0_0_0_1px_rgba(255,255,255,0.02)]">
      <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
        <div className="min-w-0">
          <div className="text-xs font-medium uppercase tracking-[0.18em] text-neutral-500">Style Rule {index + 1}</div>
          <div className="mt-2 truncate font-mono text-sm text-neutral-200">{rule.pattern || "No pattern yet"}</div>
        </div>
        <Button className="border-red-900 bg-red-950/40 text-red-200 hover:bg-red-950" onClick={onRemove}>Remove Rule</Button>
      </div>

      <Subsection title="Matching" description="Regex pattern and validation">
        <div className="grid gap-3 md:grid-cols-2">
          <Field label="Pattern">
            <Input value={rule.pattern} onChange={(event) => onChange({ ...rule, pattern: event.target.value })} />
          </Field>
          <Field label="Regex">
            <div className={`rounded-lg border px-3 py-2 text-sm ${state ? state.ok ? "border-emerald-900 bg-emerald-950/40 text-emerald-300" : "border-red-900 bg-red-950/40 text-red-300" : "border-neutral-800 bg-neutral-950 text-neutral-500"}`}>
              {state ? state.text : "Enter a pattern"}
            </div>
          </Field>
        </div>
      </Subsection>

      <Subsection title="Comment" description="Raw text only. Escape sequences and formatting codes stay untouched.">
        <Field label="Comment">
          <RawTextArea value={rule.comment} onValueChange={(comment) => onChange({ ...rule, comment })} />
        </Field>
      </Subsection>

      <Subsection
        title="Styles"
        description={`${rule.styles.length} style item${rule.styles.length === 1 ? "" : "s"}`}
        actions={<Button onClick={() => onChange({ ...rule, styles: [...rule.styles, emptyStyle()] })}>Add Style</Button>}
      >
        <StyleListEditor items={rule.styles} onChange={(styles) => onChange({ ...rule, styles })} />
      </Subsection>
    </div>
  );
}

function MentionRuleCard({ rule, onChange, onRemove, index }) {
  const state = regexState(rule.pattern);

  return (
    <div className="space-y-3 rounded-xl border border-neutral-700 bg-neutral-900/80 p-4 shadow-[0_0_0_1px_rgba(255,255,255,0.02)]">
      <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
        <div className="min-w-0">
          <div className="text-xs font-medium uppercase tracking-[0.18em] text-neutral-500">Mention Rule {index + 1}</div>
          <div className="mt-2 truncate font-mono text-sm text-neutral-200">{rule.pattern || "No pattern yet"}</div>
        </div>
        <Button className="border-red-900 bg-red-950/40 text-red-200 hover:bg-red-950" onClick={onRemove}>Remove Rule</Button>
      </div>

      <Subsection title="Matching" description="Regex pattern, title, and cooldown">
        <div className="grid gap-3 md:grid-cols-2">
          <Field label="Pattern">
            <Input value={rule.pattern} onChange={(event) => onChange({ ...rule, pattern: event.target.value })} />
          </Field>
          <Field label="Regex">
            <div className={`rounded-lg border px-3 py-2 text-sm ${state ? state.ok ? "border-emerald-900 bg-emerald-950/40 text-emerald-300" : "border-red-900 bg-red-950/40 text-red-300" : "border-neutral-800 bg-neutral-950 text-neutral-500"}`}>
              {state ? state.text : "Enter a pattern"}
            </div>
          </Field>
        </div>
        <div className="mt-3 grid gap-3 md:grid-cols-2">
          <Field label="Title">
            <Input value={rule.title} onChange={(event) => onChange({ ...rule, title: event.target.value })} />
          </Field>
          <Field label="Cooldown">
            <Input type="number" value={rule.cooldown} onChange={(event) => onChange({ ...rule, cooldown: Number(event.target.value) || 0 })} />
          </Field>
        </div>
        <div className="mt-3">
          <Toggle label="Only target gets notified" checked={rule.onlyTarget} onChange={(onlyTarget) => onChange({ ...rule, onlyTarget })} />
        </div>
      </Subsection>

      <Subsection title="Comment" description="Raw text only. Escape sequences and formatting codes stay untouched.">
        <Field label="Comment">
          <RawTextArea value={rule.comment} onValueChange={(comment) => onChange({ ...rule, comment })} />
        </Field>
      </Subsection>

      <Subsection title="Sound" description="Notification sound for matched mentions">
        <div className="grid gap-3 md:grid-cols-4">
          <Field label="Sound Id">
            <Input value={rule.sound.id} onChange={(event) => onChange({ ...rule, sound: { ...rule.sound, id: event.target.value } })} />
          </Field>
          <Field label="Category">
            <Select options={SOUND_CATEGORIES} value={rule.sound.category} onChange={(event) => onChange({ ...rule, sound: { ...rule.sound, category: event.target.value } })} />
          </Field>
          <Field label="Volume">
            <Input type="number" step="0.1" value={rule.sound.volume} onChange={(event) => onChange({ ...rule, sound: { ...rule.sound, volume: Number(event.target.value) || 0 } })} />
          </Field>
          <Field label="Pitch">
            <Input type="number" step="0.1" value={rule.sound.pitch} onChange={(event) => onChange({ ...rule, sound: { ...rule.sound, pitch: Number(event.target.value) || 0 } })} />
          </Field>
        </div>
      </Subsection>

      <Subsection
        title="Mention Targets"
        description={`${rule.mentions.length} target${rule.mentions.length === 1 ? "" : "s"}`}
        actions={<Button onClick={() => onChange({ ...rule, mentions: [...rule.mentions, emptyMention()] })}>Add Mention</Button>}
      >
        <MentionTargetEditor items={rule.mentions} onChange={(mentions) => onChange({ ...rule, mentions })} />
      </Subsection>

      <Subsection
        title="Styles"
        description={`${rule.styles.length} style item${rule.styles.length === 1 ? "" : "s"}`}
        actions={<Button onClick={() => onChange({ ...rule, styles: [...rule.styles, emptyStyle()] })}>Add Style</Button>}
      >
        <StyleListEditor items={rule.styles} onChange={(styles) => onChange({ ...rule, styles })} />
      </Subsection>
    </div>
  );
}

function PermissionRuleEditor({ title, description, groups, onChange, createRule, renderRule }) {
  const [newKey, setNewKey] = useState("");
  const keys = Object.keys(groups);

  const addGroup = () => {
    if (!newKey.trim() || groups[newKey.trim()]) return;
    onChange({ ...groups, [newKey.trim()]: [] });
    setNewKey("");
  };

  const renameGroup = (oldKey, nextKey) => {
    const key = nextKey.trim();
    if (!key || key === oldKey || groups[key]) return;
    const next = {};
    Object.entries(groups).forEach(([name, rules]) => {
      next[name === oldKey ? key : name] = rules;
    });
    onChange(next);
  };

  const removeGroup = (key) => {
    const next = { ...groups };
    delete next[key];
    onChange(next);
  };

  const setRules = (key, rules) => onChange({ ...groups, [key]: rules });

  return (
    <Section
      title={title}
      description={description}
      actions={
        <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:items-end">
          <Input className="md:min-w-[24rem]" value={newKey} placeholder="permission node" onChange={(event) => setNewKey(event.target.value)} />
          <Button className="h-[42px] border-neutral-600 bg-white px-3 text-black hover:bg-neutral-200" onClick={addGroup}>Add Group</Button>
        </div>
      }
    >
      {keys.length === 0 && <div className="text-sm text-neutral-500">No permission groups yet.</div>}
      <div className="space-y-5">
        {keys.map((key) => (
          <div key={key} className="rounded-xl border border-neutral-700 bg-neutral-950/80 p-4">
            <div className="mb-4 rounded-lg border border-neutral-800 bg-black/30 p-3">
              <div className="flex flex-col gap-3 xl:flex-row xl:items-end xl:justify-between">
                <div className="min-w-0 flex-1">
                  <div className="text-xs font-medium uppercase tracking-[0.18em] text-neutral-500">Permission Group</div>
                  <Input className="mt-2" value={key} onChange={(event) => renameGroup(key, event.target.value)} />
                </div>
                <div className="flex flex-wrap items-end gap-2 xl:pb-[1px]">
                  <div className="rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2 text-sm text-neutral-300">
                    {groups[key].length} rule{groups[key].length === 1 ? "" : "s"}
                  </div>
                  <Button className="h-[42px] border-neutral-600 bg-white px-3 text-black hover:bg-neutral-200" onClick={() => setRules(key, [...groups[key], createRule()])}>Add Rule</Button>
                  <Button className="h-[42px] border-red-900 bg-red-950/40 px-3 text-red-200 hover:bg-red-950" onClick={() => removeGroup(key)}>Remove Group</Button>
                </div>
              </div>
            </div>
            <div className="space-y-4">
              {groups[key].length === 0 && <div className="rounded-lg border border-dashed border-neutral-800 px-4 py-6 text-sm text-neutral-500">No rules in this group yet.</div>}
              {groups[key].map((rule, index) => renderRule({
                rule,
                index,
                onChange: (nextRule) => setRules(key, groups[key].map((item, current) => current === index ? nextRule : item)),
                onRemove: () => setRules(key, groups[key].filter((_, current) => current !== index))
              }))}
            </div>
          </div>
        ))}
      </div>
    </Section>
  );
}

function JsonWorkbench({ selected, onSelected, text, onText, onApply, onCopy, onDownload, onImport, onReset, message, fileInputRef }) {
  return (
    <Section
      title="Import / Export"
      description="Paste JSON, import a file, or export the current state."
      actions={
        <div className="flex w-full flex-col gap-2 xl:w-auto xl:items-end">
          <select className={inputClass} value={selected} onChange={(event) => onSelected(event.target.value)}>
            {Object.keys(FILES).map((key) => <option key={key} value={key}>{key}.json</option>)}
          </select>
          <div className="flex flex-wrap gap-2 xl:justify-end">
            <Button onClick={() => fileInputRef.current?.click()}>Import File</Button>
            <Button onClick={onApply}>Apply</Button>
            <Button onClick={onCopy}>Copy</Button>
            <Button onClick={onDownload}>Download</Button>
            <Button onClick={onReset}>Reset All</Button>
          </div>
        </div>
      }
    >
      <input ref={fileInputRef} type="file" accept=".json,application/json" className="hidden" onChange={onImport} />
      {message && <div className="rounded-lg border border-neutral-800 bg-neutral-950 px-3 py-2 text-sm text-neutral-300">{message}</div>}
      <TextArea value={text} onChange={(event) => onText(event.target.value)} className="min-h-[28rem]" />
    </Section>
  );
}

function ConfigEditor() {
  const fileInputRef = useRef(null);
  const playerProfileCacheRef = useRef({});
  const [defaults, setDefaults] = useState(null);
  const [config, setConfig] = useState(null);
  const [styles, setStyles] = useState(null);
  const [mentions, setMentions] = useState(null);
  const [presets, setPresets] = useState(null);
  const [tab, setTab] = useState("general");
  const [jsonFile, setJsonFile] = useState("config");
  const [jsonText, setJsonText] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const [configData, stylesData, mentionsData, presetsData] = await Promise.all([
          fetchJson(FILES.config),
          fetchJson(FILES.styles),
          fetchJson(FILES.mentions),
          fetchJson(FILES.presets)
        ]);

        const nextDefaults = {
          config: normalizeConfig(configData),
          styles: normalizeStyles(stylesData),
          mentions: normalizeMentions(mentionsData),
          presets: normalizePresets(presetsData)
        };

        setDefaults(nextDefaults);
        setConfig(clone(nextDefaults.config));
        setStyles(clone(nextDefaults.styles));
        setMentions(clone(nextDefaults.mentions));
        setPresets(clone(nextDefaults.presets));
      } catch (error) {
        setMessage(error.message);
      }
    })();
  }, []);

  const dataMap = useMemo(() => ({ config, styles, mentions, presets }), [config, styles, mentions, presets]);

  useEffect(() => {
    if (dataMap[jsonFile]) setJsonText(stringify(dataMap[jsonFile]));
  }, [jsonFile, dataMap]);

  if (!config || !styles || !mentions || !presets) {
    return (
      <div className="min-h-screen bg-black px-4 py-10 text-neutral-100">
        <div className="mx-auto max-w-5xl rounded-2xl border border-neutral-800 bg-neutral-950 p-6">
          <h1 className="text-2xl font-semibold">Config Generator</h1>
          <p className="mt-2 text-sm text-neutral-400">{message || "Loading..."}</p>
        </div>
      </div>
    );
  }

  const setUuidList = (key, values) => setConfig({ ...config, [key]: values });
  const renameObjectKey = (source, oldKey, newKey) => {
    const key = newKey.trim();
    if (!key || key === oldKey || source[key] !== undefined) return source;
    const next = {};
    Object.entries(source).forEach(([name, value]) => {
      next[name === oldKey ? key : name] = value;
    });
    return next;
  };

  const fetchPlayerProfile = async (uuid) => {
    const normalizedUuid = uuid.replace(/-/g, "");
    const cachedProfile = playerProfileCacheRef.current[normalizedUuid];
    if (cachedProfile) return cachedProfile;

    const profileApis = [
      {
        url: `https://playerdb.co/api/player/minecraft/${normalizedUuid}`,
        pickName: (json) => json?.data?.player?.username || json?.data?.player?.raw_id
      },
      {
        url: `https://api.ashcon.app/mojang/v2/user/${normalizedUuid}`,
        pickName: (json) => json?.username || json?.name
      },
      {
        url: `https://sessionserver.mojang.com/session/minecraft/profile/${normalizedUuid}`,
        pickName: (json) => json?.name
      }
    ];

    const failures = [];
    let resolvedName = null;

    for (const api of profileApis) {
      try {
        const response = await fetch(api.url, { headers: { Accept: "application/json" } });
        if (!response.ok) {
          failures.push(`HTTP ${response.status}`);
          continue;
        }
        const json = await response.json();
        const name = api.pickName(json);
        if (name) {
          resolvedName = name;
          break;
        }
      } catch (error) {
        failures.push(error.message || "request failed");
      }
    }

    if (!resolvedName) {
      throw new Error(`Could not resolve this UUID. ${failures.join(" | ")}`);
    }

    const profile = {
      uuid,
      normalizedUuid,
      name: resolvedName,
      headUrl: `https://mc-heads.net/head/${normalizedUuid}/96`
    };

    playerProfileCacheRef.current[normalizedUuid] = profile;
    return profile;
  };

  const applyJson = () => {
    try {
      const parsed = JSON.parse(jsonText);
      if (jsonFile === "config") setConfig(normalizeConfig(parsed));
      if (jsonFile === "styles") setStyles(normalizeStyles(parsed));
      if (jsonFile === "mentions") setMentions(normalizeMentions(parsed));
      if (jsonFile === "presets") setPresets(normalizePresets(parsed));
      setMessage(`${jsonFile}.json applied.`);
    } catch (error) {
      setMessage(error.message);
    }
  };

  const importJson = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    const text = await file.text();
    setJsonText(text);
    setMessage(`${file.name} loaded.`);
    event.target.value = "";
  };

  const copyJson = async () => {
    await navigator.clipboard.writeText(jsonText);
    setMessage(`${jsonFile}.json copied.`);
  };

  const downloadJson = () => kebabDownload(jsonFile, jsonText);
  const resetAll = () => {
    if (!defaults) return;
    setConfig(clone(defaults.config));
    setStyles(clone(defaults.styles));
    setMentions(clone(defaults.mentions));
    setPresets(clone(defaults.presets));
    setMessage("Reset to defaults.");
  };

  const tabs = [
    ["general", "General"],
    ["styles", "Styles"],
    ["mentions", "Mentions"],
    ["json", "JSON"]
  ];

  return (
    <div className="min-h-screen bg-black px-3 py-6 text-neutral-100 md:px-4 md:py-8 xl:px-5">
      <div className="mx-auto max-w-[108rem] space-y-4">
        <header className={`${shellClass} p-4 md:p-5`}>
          <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
            <div>
              <div className="text-xs uppercase tracking-[0.2em] text-neutral-500">Embellish Chat</div>
              <h1 className="mt-2 text-3xl font-semibold">Config Generator</h1>
            </div>
            <div className="flex flex-wrap gap-2">
              {tabs.map(([id, label]) => (
                <button
                  key={id}
                  type="button"
                  onClick={() => setTab(id)}
                  className={`rounded-lg px-3 py-2 text-sm font-medium transition ${tab === id ? "bg-white text-black" : "border border-neutral-800 bg-neutral-950 text-neutral-200 hover:border-neutral-600"}`}
                >
                  {label}
                </button>
              ))}
            </div>
          </div>
        </header>

        {tab === "general" && (
          <>
            <Section title="Global Config" description="Core settings from config.json">
              <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
                <Field label="Version"><Input value={config.version} onChange={(event) => setConfig({ ...config, version: event.target.value })} /></Field>
                <Field label="Delimiter"><Input value={config.delimiter} onChange={(event) => setConfig({ ...config, delimiter: event.target.value })} /></Field>
                <Field label="Timestamp"><Input value={config.timestamp} onChange={(event) => setConfig({ ...config, timestamp: event.target.value })} /></Field>
                <Field label="Command Alias"><Input value={config.command_alias} onChange={(event) => setConfig({ ...config, command_alias: event.target.value })} /></Field>
                <Field label="URL Color"><ColorInput value={config.url_color} onChange={(value) => setConfig({ ...config, url_color: value })} /></Field>
                <Field label="Team Color"><ColorInput value={config.team_color} onChange={(value) => setConfig({ ...config, team_color: value })} /></Field>
              </div>
              <div className="grid gap-3 md:grid-cols-3">
                <Toggle label="Notify command enabled" checked={config.notify_command_enabled} onChange={(value) => setConfig({ ...config, notify_command_enabled: value })} />
                <Toggle label="Notify mention enabled" checked={config.notify_mention_enabled} onChange={(value) => setConfig({ ...config, notify_mention_enabled: value })} />
                <Toggle label="Disable vanilla chat format" checked={config.disable_vanilla_chat_format} onChange={(value) => setConfig({ ...config, disable_vanilla_chat_format: value })} />
              </div>
            </Section>

            <Section title="Presets" description="prefix, whitelist, icon, item, and color">
              <div className="space-y-5">
                <KeyValueEditor
                  title="Prefixes"
                  values={presets.prefix}
                  keyPlaceholder="key"
                  valuePlaceholder="value"
                  onAdd={() => setPresets({ ...presets, prefix: { ...presets.prefix, [`key-${Object.keys(presets.prefix).length + 1}`]: "" } })}
                  onRename={(oldKey, newKey) => setPresets({ ...presets, prefix: renameObjectKey(presets.prefix, oldKey, newKey) })}
                  onChange={(key, value) => setPresets({ ...presets, prefix: { ...presets.prefix, [key]: value } })}
                  onRemove={(key) => {
                    const next = { ...presets.prefix };
                    delete next[key];
                    setPresets({ ...presets, prefix: next });
                  }}
                />
                <ListTextEditor
                  title="Whitelist"
                  values={presets.whitelist}
                  placeholder="whitelist entry"
                  onAdd={(value) => setPresets({ ...presets, whitelist: [...presets.whitelist, value] })}
                  onChange={(index, value) => setPresets({ ...presets, whitelist: presets.whitelist.map((item, current) => current === index ? value : item) })}
                  onRemove={(index) => setPresets({ ...presets, whitelist: presets.whitelist.filter((_, current) => current !== index) })}
                />
                <AtlasEditor
                  title="Icon Presets"
                  values={presets.icon}
                  onAdd={() => setPresets({ ...presets, icon: { ...presets.icon, [`icon-${Object.keys(presets.icon).length + 1}`]: { atlas: "minecraft:gui", sprite: "" } } })}
                  onRename={(oldKey, newKey) => setPresets({ ...presets, icon: renameObjectKey(presets.icon, oldKey, newKey) })}
                  onChange={(key, field, value) => setPresets({ ...presets, icon: { ...presets.icon, [key]: { ...presets.icon[key], [field]: value } } })}
                  onRemove={(key) => {
                    const next = { ...presets.icon };
                    delete next[key];
                    setPresets({ ...presets, icon: next });
                  }}
                />
                <AtlasEditor
                  title="Item Sprite Overrides"
                  values={presets.item}
                  onAdd={() => setPresets({ ...presets, item: { ...presets.item, [`minecraft:item_${Object.keys(presets.item).length + 1}`]: { atlas: "minecraft:items", sprite: "" } } })}
                  onRename={(oldKey, newKey) => setPresets({ ...presets, item: renameObjectKey(presets.item, oldKey, newKey) })}
                  onChange={(key, field, value) => setPresets({ ...presets, item: { ...presets.item, [key]: { ...presets.item[key], [field]: value } } })}
                  onRemove={(key) => {
                    const next = { ...presets.item };
                    delete next[key];
                    setPresets({ ...presets, item: next });
                  }}
                />
                <ColorPresetEditor
                  values={presets.color}
                  onAdd={() => setPresets({ ...presets, color: { ...presets.color, [`preset-${Object.keys(presets.color).length + 1}`]: "#FFFFFF" } })}
                  onRename={(oldKey, newKey) => setPresets({ ...presets, color: renameObjectKey(presets.color, oldKey, newKey) })}
                  onChange={(key, value) => setPresets({ ...presets, color: { ...presets.color, [key]: value } })}
                  onRemove={(key) => {
                    const next = { ...presets.color };
                    delete next[key];
                    setPresets({ ...presets, color: next });
                  }}
                />
              </div>
            </Section>

            <Section title="Players" description="Player UUID utilities stay at the bottom, with head and name lookup restored.">
              <div className="space-y-5">
                <PlayerListEditor
                  title="Banned Players"
                  values={config.banned_players}
                  placeholder="UUID (e.g. 123e4567-e89b-12d3-a456-426614174000)"
                  fetchPlayerProfile={fetchPlayerProfile}
                  onAdd={(value) => setUuidList("banned_players", [...config.banned_players, value])}
                  onChange={(index, value) => setUuidList("banned_players", config.banned_players.map((item, current) => current === index ? value : item))}
                  onRemove={(index) => setUuidList("banned_players", config.banned_players.filter((_, current) => current !== index))}
                />
                <PlayerListEditor
                  title="Notify-Off Players"
                  values={config.notify_off_players}
                  placeholder="UUID (e.g. 123e4567-e89b-12d3-a456-426614174000)"
                  fetchPlayerProfile={fetchPlayerProfile}
                  onAdd={(value) => setUuidList("notify_off_players", [...config.notify_off_players, value])}
                  onChange={(index, value) => setUuidList("notify_off_players", config.notify_off_players.map((item, current) => current === index ? value : item))}
                  onRemove={(index) => setUuidList("notify_off_players", config.notify_off_players.filter((_, current) => current !== index))}
                />
              </div>
            </Section>
          </>
        )}

        {tab === "styles" && (
          <PermissionRuleEditor
            title="Style Rules"
            description="Each permission node maps to a list of regex style rules."
            groups={styles.style_rules}
            onChange={(style_rules) => setStyles({ ...styles, style_rules })}
            createRule={emptyStyleRule}
            renderRule={({ rule, onChange, onRemove }) => <StyleRuleCard rule={rule} onChange={onChange} onRemove={onRemove} />}
          />
        )}

        {tab === "mentions" && (
          <PermissionRuleEditor
            title="Mention Rules"
            description="Mention targets now include the PERMISSION type."
            groups={mentions.mention_rules}
            onChange={(mention_rules) => setMentions({ ...mentions, mention_rules })}
            createRule={emptyMentionRule}
            renderRule={({ rule, onChange, onRemove }) => <MentionRuleCard rule={rule} onChange={onChange} onRemove={onRemove} />}
          />
        )}

        {tab === "json" && (
          <JsonWorkbench
            selected={jsonFile}
            onSelected={setJsonFile}
            text={jsonText}
            onText={setJsonText}
            onApply={applyJson}
            onCopy={copyJson}
            onDownload={downloadJson}
            onImport={importJson}
            onReset={resetAll}
            message={message}
            fileInputRef={fileInputRef}
          />
        )}
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<ConfigEditor />);
