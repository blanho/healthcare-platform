

import { PASSWORD_RULES } from '../constants';

export interface PasswordStrength {
  score: number;
  label: 'Very Weak' | 'Weak' | 'Fair' | 'Strong' | 'Very Strong';
  color: string;
  feedback: string[];
}

export function checkPasswordStrength(password: string): PasswordStrength {
  const feedback: string[] = [];
  let score = 0;

  if (password.length >= PASSWORD_RULES.MIN_LENGTH) {
    score += 1;
  } else {
    feedback.push(`Use at least ${PASSWORD_RULES.MIN_LENGTH} characters`);
  }

  if (password.length >= 12) {
    score += 1;
  }

  if (/[A-Z]/.test(password)) {
    score += 0.5;
  } else {
    feedback.push('Add an uppercase letter');
  }

  if (/[a-z]/.test(password)) {
    score += 0.5;
  } else {
    feedback.push('Add a lowercase letter');
  }

  if (/\d/.test(password)) {
    score += 0.5;
  } else {
    feedback.push('Add a number');
  }

  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    score += 0.5;
  } else {
    feedback.push('Add a special character for extra security');
  }

  // Normalize score to 0-4
  const normalizedScore = Math.min(4, Math.floor(score));

  const strengthMap: Record<number, Pick<PasswordStrength, 'label' | 'color'>> = {
    0: { label: 'Very Weak', color: '#EF4444' },
    1: { label: 'Weak', color: '#F97316' },
    2: { label: 'Fair', color: '#FBBF24' },
    3: { label: 'Strong', color: '#22C55E' },
    4: { label: 'Very Strong', color: '#059669' },
  };

  return {
    score: normalizedScore,
    ...strengthMap[normalizedScore],
    feedback: feedback.slice(0, 2), // Show max 2 suggestions
  };
}

/**
 * Generate a random secure password
 * @param length - Password length (default: 16)
 * @returns Random password string
 */
export function generateSecurePassword(length = 16): string {
  const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const lowercase = 'abcdefghijklmnopqrstuvwxyz';
  const numbers = '0123456789';
  const special = '!@#$%^&*()_+-=[]{}|;:,.<>?';

  const allChars = uppercase + lowercase + numbers + special;

  // Ensure at least one of each required type
  let password = '';
  password += uppercase[Math.floor(Math.random() * uppercase.length)];
  password += lowercase[Math.floor(Math.random() * lowercase.length)];
  password += numbers[Math.floor(Math.random() * numbers.length)];
  password += special[Math.floor(Math.random() * special.length)];

  // Fill the rest randomly
  for (let i = password.length; i < length; i++) {
    password += allChars[Math.floor(Math.random() * allChars.length)];
  }

  // Shuffle the password
  return password
    .split('')
    .sort(() => Math.random() - 0.5)
    .join('');
}

/**
 * Check if password contains common patterns (weak)
 * @param password - Password to check
 * @returns true if password contains weak patterns
 */
export function hasWeakPatterns(password: string): boolean {
  const weakPatterns = [
    /^123456/,
    /password/i,
    /qwerty/i,
    /abc123/i,
    /(.)\1{2,}/, // 3+ repeating characters
    /^(.+?)\1+$/, // Repeating pattern
  ];

  return weakPatterns.some((pattern) => pattern.test(password));
}
