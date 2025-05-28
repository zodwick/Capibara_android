import type { Metadata } from "next";
import { Inter, Playfair_Display } from "next/font/google";
import "./globals.css";

const inter = Inter({ 
  subsets: ["latin"],
  variable: "--font-inter",
});

const playfair = Playfair_Display({ 
  subsets: ["latin"],
  variable: "--font-playfair",
});

export const metadata: Metadata = {
  title: "Capybara Sanctuary - Gentle Digital Wellness",
  description: "Find peace in the digital world with your capybara sanctuary. A mindful approach to screen time management that feels natural and sustainable.",
  keywords: ["digital wellness", "screen time", "mindfulness", "capybara", "mental health", "digital detox", "peaceful technology"],
  authors: [{ name: "Capybara Sanctuary Team" }],
  creator: "Capybara Sanctuary",
  publisher: "Capybara Sanctuary",
  openGraph: {
    title: "Capybara Sanctuary - Gentle Digital Wellness",
    description: "Find peace in the digital world with your capybara sanctuary. A mindful approach to screen time management.",
    url: "https://capybarasanctuary.app",
    siteName: "Capybara Sanctuary",
    images: [
      {
        url: "/images/capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg.png",
        width: 1200,
        height: 630,
        alt: "Peaceful capybara in meditation pose",
      },
    ],
    locale: "en_US",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "Capybara Sanctuary - Gentle Digital Wellness",
    description: "Find peace in the digital world with your capybara sanctuary.",
    images: ["/images/capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg.png"],
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-video-preview": -1,
      "max-image-preview": "large",
      "max-snippet": -1,
    },
  },
  verification: {
    google: "your-google-verification-code",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${inter.variable} ${playfair.variable}`}>
      <body className={`${inter.className} antialiased`}>
        {children}
      </body>
    </html>
  );
}
