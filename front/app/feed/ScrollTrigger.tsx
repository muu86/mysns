import { useCallback } from 'react';

export function ScrollTrigger({ fetchNext }: { fetchNext: Function }) {
  const triggerRef = useCallback(
    (node: HTMLDivElement) => {
      if (!node) return;
      console.log('triggered');

      const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            console.log('fetch inside trigger');
            fetchNext();
          }
        });
      });

      observer.observe(node);

      return () => {
        observer.disconnect();
      };
    },
    [fetchNext]
  );

  return (
    <div
      ref={triggerRef}
      className="w-full h-32 flex justify-center items-center"
    >
      <div className="animate-spin w-6 h-6">
        <svg width="24" height="24" viewBox="0 0 24 24">
          <g>
            <circle cx="12" cy="2.5" r="1.5" opacity=".14" />
            <circle cx="16.75" cy="3.77" r="1.5" opacity=".29" />
            <circle cx="20.23" cy="7.25" r="1.5" opacity=".43" />
            <circle cx="21.50" cy="12.00" r="1.5" opacity=".57" />
            <circle cx="20.23" cy="16.75" r="1.5" opacity=".71" />
            <circle cx="16.75" cy="20.23" r="1.5" opacity=".86" />
            <circle cx="12" cy="21.5" r="1.5" />
          </g>
        </svg>
      </div>
    </div>
  );
}
