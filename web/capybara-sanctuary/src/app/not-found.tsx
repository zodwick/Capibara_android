import Image from "next/image";
import Link from "next/link";

export default function NotFound() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50 flex items-center justify-center">
      <div className="max-w-2xl mx-auto px-6 text-center">
        <div className="mb-8">
          <Image
            src="/images/capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg.png"
            alt="Confused capybara"
            width={200}
            height={200}
            className="mx-auto"
          />
        </div>
        
        <h1 className="text-4xl md:text-5xl font-serif text-amber-900 mb-6">
          Oops! This capybara got lost
        </h1>
        
        <p className="text-lg text-amber-800 mb-8 leading-relaxed">
          Even the most peaceful creatures sometimes wander off the path. 
          Let's help this little one find their way back to the sanctuary.
        </p>
        
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link 
            href="/"
            className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-4 rounded-2xl font-medium transition-all duration-300 shadow-lg hover:shadow-xl text-center"
          >
            Return to Sanctuary
          </Link>
          <Link 
            href="/about"
            className="border-2 border-amber-600 text-amber-700 hover:bg-amber-50 px-8 py-4 rounded-2xl font-medium transition-all duration-300 text-center"
          >
            Learn About Us
          </Link>
        </div>
        
        <p className="text-sm text-amber-600 mt-8">
          Error 404 • Page not found
        </p>
      </div>
    </div>
  );
} 