import { useState, useEffect, useRef } from 'react';

interface UseCounterOptions {
  duration?: number;
  start?: number;
  threshold?: number;
}

interface UseCounterReturn {
  count: number;
  ref: React.RefObject<HTMLDivElement | null>;
}

export function useCounter(
  end: number,
  options: UseCounterOptions = {}
): UseCounterReturn {
  const { duration = 2000, start = 0, threshold = 0.3 } = options;

  const [count, setCount] = useState(start);
  const countRef = useRef<HTMLDivElement>(null);
  const hasAnimated = useRef(false);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !hasAnimated.current) {
          hasAnimated.current = true;
          const startTime = performance.now();

          const animate = (currentTime: number) => {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);

            const easeOut = 1 - Math.pow(1 - progress, 3);
            setCount(Math.floor(start + (end - start) * easeOut));

            if (progress < 1) {
              requestAnimationFrame(animate);
            }
          };

          requestAnimationFrame(animate);
        }
      },
      { threshold }
    );

    if (countRef.current) {
      observer.observe(countRef.current);
    }

    return () => observer.disconnect();
  }, [end, duration, start, threshold]);

  return { count, ref: countRef };
}
