import type { Config } from 'tailwindcss';

/**
 * Healthcare Platform Design System
 * 
 * Style: Accessible & Ethical (WCAG AAA compliant)
 * Primary: Cyan-600 (#0891B2) - Trust, clarity, healthcare
 * Secondary: Emerald-600 (#059669) - Growth, health, vitality
 * Fonts: Figtree (headings) + Noto Sans (body)
 */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Primary palette - Cyan (Trust & Healthcare)
        primary: {
          50: '#ecfeff',
          100: '#cffafe',
          200: '#a5f3fc',
          300: '#67e8f9',
          400: '#22d3ee',
          500: '#06b6d4',
          600: '#0891b2', // Main
          700: '#0e7490',
          800: '#155e75',
          900: '#164e63',
          950: '#083344',
          DEFAULT: '#0891b2',
        },
        // Secondary palette - Emerald (Health & Vitality)
        secondary: {
          50: '#ecfdf5',
          100: '#d1fae5',
          200: '#a7f3d0',
          300: '#6ee7b7',
          400: '#34d399',
          500: '#10b981',
          600: '#059669', // Main
          700: '#047857',
          800: '#065f46',
          900: '#064e3b',
          950: '#022c22',
          DEFAULT: '#059669',
        },
        // Semantic colors
        success: {
          light: '#dcfce7',
          DEFAULT: '#16a34a',
          dark: '#14532d',
        },
        warning: {
          light: '#fef3c7',
          DEFAULT: '#d97706',
          dark: '#78350f',
        },
        error: {
          light: '#fee2e2',
          DEFAULT: '#dc2626',
          dark: '#7f1d1d',
        },
        info: {
          light: '#dbeafe',
          DEFAULT: '#2563eb',
          dark: '#1e3a8a',
        },
        // Background colors
        background: {
          DEFAULT: '#ffffff',
          paper: '#f8fafc',
          subtle: '#f1f5f9',
          dark: '#0f172a',
          'dark-paper': '#1e293b',
          'dark-subtle': '#334155',
        },
        // Text colors (WCAG AAA compliant)
        text: {
          primary: '#0f172a', // slate-900
          secondary: '#475569', // slate-600
          muted: '#64748b', // slate-500
          disabled: '#94a3b8', // slate-400
          'dark-primary': '#f8fafc', // slate-50
          'dark-secondary': '#cbd5e1', // slate-300
          'dark-muted': '#94a3b8', // slate-400
        },
        // Border colors
        border: {
          DEFAULT: '#e2e8f0', // slate-200
          dark: '#334155', // slate-700
        },
      },
      fontFamily: {
        heading: [
          'Figtree',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          'sans-serif',
        ],
        body: [
          'Noto Sans',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          'sans-serif',
        ],
        mono: [
          'JetBrains Mono',
          'Fira Code',
          'Consolas',
          'Monaco',
          'monospace',
        ],
      },
      fontSize: {
        // Type scale based on 1.250 ratio (Major Third)
        xs: ['0.75rem', { lineHeight: '1rem' }], // 12px
        sm: ['0.875rem', { lineHeight: '1.25rem' }], // 14px
        base: ['1rem', { lineHeight: '1.5rem' }], // 16px
        lg: ['1.125rem', { lineHeight: '1.75rem' }], // 18px
        xl: ['1.25rem', { lineHeight: '1.75rem' }], // 20px
        '2xl': ['1.5rem', { lineHeight: '2rem' }], // 24px
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }], // 30px
        '4xl': ['2.25rem', { lineHeight: '2.5rem' }], // 36px
        '5xl': ['3rem', { lineHeight: '1.16' }], // 48px
        '6xl': ['3.75rem', { lineHeight: '1.1' }], // 60px
      },
      spacing: {
        // 4px base unit
        '4.5': '1.125rem', // 18px
        '13': '3.25rem', // 52px
        '15': '3.75rem', // 60px
        '18': '4.5rem', // 72px
        '22': '5.5rem', // 88px
      },
      borderRadius: {
        '4xl': '2rem',
        '5xl': '2.5rem',
      },
      boxShadow: {
        'soft-sm': '0 1px 2px 0 rgb(0 0 0 / 0.03)',
        soft: '0 1px 3px 0 rgb(0 0 0 / 0.05), 0 1px 2px -1px rgb(0 0 0 / 0.05)',
        'soft-md': '0 4px 6px -1px rgb(0 0 0 / 0.05), 0 2px 4px -2px rgb(0 0 0 / 0.05)',
        'soft-lg': '0 10px 15px -3px rgb(0 0 0 / 0.05), 0 4px 6px -4px rgb(0 0 0 / 0.05)',
        'soft-xl': '0 20px 25px -5px rgb(0 0 0 / 0.05), 0 8px 10px -6px rgb(0 0 0 / 0.05)',
        // Card shadows
        card: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
        'card-hover': '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
        // Focus ring
        'focus-ring': '0 0 0 3px rgba(8, 145, 178, 0.4)',
      },
      animation: {
        'fade-in': 'fadeIn 0.2s ease-out',
        'fade-in-up': 'fadeInUp 0.3s ease-out',
        'slide-in-right': 'slideInRight 0.3s ease-out',
        'scale-in': 'scaleIn 0.2s ease-out',
        pulse: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideInRight: {
          '0%': { opacity: '0', transform: 'translateX(10px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        scaleIn: {
          '0%': { opacity: '0', transform: 'scale(0.95)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
      },
      transitionDuration: {
        DEFAULT: '200ms',
      },
      transitionTimingFunction: {
        DEFAULT: 'cubic-bezier(0.4, 0, 0.2, 1)',
      },
    },
  },
  plugins: [],
} satisfies Config;
