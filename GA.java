import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.Random;
import java.lang.Math;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

	Polygon polygon;
	Color color;

	public MyPolygon(Polygon _p, Color _c) {
		polygon = _p;
		color = _c;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color _c) {
		color = _c;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setShape(Polygon _p) {
		polygon = _p;
	}
	public MyPolygon cloneMe()
	{
		//MyPolygon newMe = new MyPolygon((Polygon)polygon.clone(), (Color)color.clone());
		Polygon copyPolygon = new Polygon(polygon.xpoints.clone(),polygon.ypoints.clone(),polygon.npoints);
		Color copyColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
		//System.out.println("polygon x points: " + copyPolygon.xpoints[0] + " color red: " + copyColor.getRed());
		MyPolygon newMe = new MyPolygon(copyPolygon, copyColor);
		//newMe = new MyPolygon(polygon, color);
		return newMe;

	}

}


// Each GASolution has a list of MyPolygon objects
class GASolution {

	ArrayList<MyPolygon> shapes;

	// width and height are for the full resulting image
	int width, height;

	public GASolution(int _width, int _height) {
		shapes = new ArrayList<MyPolygon>();
		width = _width;
		height = _height;
	}

	public void addPolygon(MyPolygon p) {
		shapes.add(p);
	}

	public ArrayList<MyPolygon> getShapes() {
		return shapes;
	}

	public int size() {
		return shapes.size();
	}

	// Create a BufferedImage of this solution
	// Use this to compare an evolved solution with
	// a BufferedImage of the target image
	//
	// This is almost surely NOT the fastest way to do this...
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (MyPolygon p : shapes) {
			Graphics g2 = image.getGraphics();
			g2.setColor(p.getColor());
			Polygon poly = p.getPolygon();
			if (poly.npoints > 0) {
				g2.fillPolygon(poly);
			}
		}
		return image;
	}

	public String toString() {
		return "" + shapes;
	}
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{

    int width, height;
    GASolution solution;

    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
    	width = WINDOW_WIDTH;
    	height = WINDOW_HEIGHT;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setImage(GASolution sol) {
  	    solution = sol;
    }

    public void paintComponent(Graphics g) {
		BufferedImage image = solution.getImage();
		g.drawImage(image, 0, 0, null);
    }
}


public class GA extends JComponent{

		Random random = new Random();

    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;
		ArrayList<Double> fitnesses;

    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = .5;	//mutation start here and slow to
		double MIN_MUTATE = .01;	//this one
    double CROSSOVER_RATE = 0.6;
    int POLYGON_POINTS = 3;
    int MAX_POLYGONS = 10;
		int POPULATION = 100;

    public GA(GACanvas _canvas, BufferedImage _realPicture) {
        canvas = _canvas;
        realPicture = _realPicture;
        width = realPicture.getWidth();
        height = realPicture.getHeight();
        population = new ArrayList<GASolution>();

        // You'll need to define the following functions
        createPopulation(POPULATION);	// Make 50 new, random chromosomes
    }
		public MyPolygon makeRandomGon(int sides)
		{
			int[] xpoints = new int[sides];
			int[] ypoints = new int[sides];
			for (int i = 0; i<sides; i++)
			{
				xpoints[i] = random.nextInt(width);
				ypoints[i] = random.nextInt(height);
			}
			Polygon poly = new Polygon(xpoints, ypoints, sides);
			Color col = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
			return new MyPolygon(poly, col);
		}
		public GASolution makeRandomCritter()
		{
			GASolution critter = new GASolution(width, height);
			for (int i=0; i<MAX_POLYGONS; i++)
			{
				MyPolygon gon = makeRandomGon(POLYGON_POINTS);
				critter.addPolygon(gon);
			}
			return critter;
		}

		public void createPopulation(int count)
		{
			for (int i = 0; i<count; i++)
			{
				population.add(makeRandomCritter());
			}
		}
		public int compareColors(Color c1, Color c2)
		{
			int difference = 0;
			int[] cc1;
			int[] cc2;
			cc1 = new int[]{c1.getRed(),c1.getGreen(),c1.getBlue()};
			cc2 = new int[]{c2.getRed(),c2.getGreen(),c2.getBlue()};
			//System.out.println("color 1: " + cc1[0]);
			//System.out.println("color 2: " + cc2[0]);
			for (int i=0; i< 3; i++)
			{
				difference += Math.abs(cc1[i]-cc2[i]);
			}
		//	System.out.println("difference + " + difference);
			return difference;
		}
		public double GetAverageFitness()
		{
			double total = 0;
			for (double fitness : fitnesses)
			 	total += fitness;
			return total/POPULATION;
		}
    public double Fitness(GASolution critter){
			BufferedImage mypic = critter.getImage();
			int unfitness = 0;
			for (int i=0; i< 100; i++)
			{
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				Color mycolor = new Color(mypic.getRGB(x,y));
				Color realcolor = new Color(realPicture.getRGB(x,y));

				unfitness += compareColors(mycolor,realcolor);
			}
			//int fitness = 255*3*100-unfitness;
			double fitness = 100/((double)unfitness);
			fitness = fitness*fitness;
			//System.out.println("unfitness "+unfitness + " | fitness " + fitness);
			return fitness;
		}
		public void getFitnesses(){
			fitnesses = new ArrayList();
			for (GASolution critter : population)
			{
			 	double fit = Fitness(critter);
				fitnesses.add(fit);
			}
			// int worst = GetWorstSolution();
			// for (int fitness : fitnesses)
			// {
			// 	fitness -= worst;
			// }
		}

		public GASolution Mutate(GASolution baby)
		{
			//GASolution mutantBaby = new GASolution(width, height);
			for (MyPolygon shape : baby.getShapes())
			{
				double rd = random.nextDouble();
				//System.out.println("random double is " + rd);
				if (rd < MUTATION_RATE/2)	//mutate color
				{
					Color mycolor = shape.getColor();
					int rand = random.nextInt(3);
					int red = mycolor.getRed();
					int green = mycolor.getGreen();
					int blue = mycolor.getBlue();
					if (rand == 0)
						red = Math.max(Math.min((red + ((int)(random.nextGaussian()*255/2))), 255), 0);
					else if (rand == 1)
						green = Math.max(Math.min((green + ((int)(random.nextGaussian()*255/2))), 255), 0);
					else
						blue = Math.max(Math.min((blue + ((int)(random.nextGaussian()*255/2))), 255), 0);
					//System.out.println(red + " " + green + " " + blue);
					Color col = new Color(red,green,blue);
					shape.setColor(col);
				}
				else if (rd < MUTATION_RATE)	//mutate shape
				{
					// int sides = POLYGON_POINTS;
					// int[] xpoints = new int[sides];
					// int[] ypoints = new int[sides];
					// for (int i = 0; i<sides; i++)
					// {
					// 	xpoints[i] = random.nextInt(width);
					// 	ypoints[i] = random.nextInt(height);
					// }
					// Polygon pol = new Polygon(xpoints, ypoints, sides);
					// shape.setShape(pol);
					int pointNum = random.nextInt(POLYGON_POINTS);
					shape.polygon.xpoints[pointNum] += (int)(random.nextGaussian()*width/5);
					shape.polygon.ypoints[pointNum] += (int)(random.nextGaussian()*height/5);
				}
			}
			return baby;
		}
		public GASolution Mate(GASolution c1, GASolution c2)
		{
			MyPolygon newGon;
			GASolution baby = new GASolution(width, height);
			ArrayList <MyPolygon> shapes1 = c1.getShapes();
			ArrayList <MyPolygon> shapes2 = c2.getShapes();
			// for (int i = 0; i < shapes1.size()/2; i++)	//static split
			// {
			// 		baby.addPolygon(shapes1.get(i));
			// }
			// for (int i = shapes2.size()/2; i < shapes2.size(); i++)
			// {
			// 		baby.addPolygon(shapes2.get(i));
			// }
			for (int i = 0; i < shapes1.size(); i++)	//random chromosones
			{
				if (random.nextBoolean())
					newGon = shapes1.get(i).cloneMe();
				else
					newGon = shapes2.get(i).cloneMe();
				baby.addPolygon(newGon);
				// System.out.println(shapes1.get(i).getColor() + "polygon: " + shapes1.get(i).getPolygon());
				// System.out.println(shapes2.get(i).getColor() + "polygon: " + shapes2.get(i).getPolygon());
				// System.out.println(baby.getShapes().get(i).getColor() + "polygon: " + baby.getShapes().get(i).getPolygon());
			}

			//System.out.println("P1: "+ Fitness(c1) + "breeds with p2: " + Fitness(c2) + " to produce: " + Fitness(baby));
			return baby;
		}
		public GASolution pickParent()
		{
			double sum = 0;
			//System.out.println("fits: " + fitnesses);
			for (double fit : fitnesses)
				sum += fit;
			//System.out.println("sum: " + sum);
			double pick = random.nextDouble()*sum;
			int i = -1;
			while (pick > 0)
			{
				i++;
				pick-= fitnesses.get(i);
			}
			//System.out.println("picked parent with fitness: " + fitnesses.get(i));
			return population.get(i);
		}
		public void NewPopulation(){
			ArrayList<GASolution> newPop = new ArrayList();
			//double fitnessChange;
			GASolution p1;
			GASolution p2;
			GASolution child;
			int i = 0;
			while (i< population.size()*CROSSOVER_RATE)
			{
				i++;
				// do {
					p1 = pickParent();
					p2 = pickParent();
					child = Mate(p1, p2);	//this works well
					//fitnessChange = 2*Fitness(child)/(Fitness(p1)+Fitness(p2));
				// } while (fitnessChange<1);
				//System.out.println("change: "+fitnessChange);
				child = Mutate(child);
				newPop.add(child);

			}
			while (newPop.size()<population.size())
			{
				newPop.add(pickParent());
			}
			//System.out.print(i + "population size is " + population.size() + " size of array is "+ newPop.size());
			population = newPop;
		}

		public int GetBestSolution(){
			double best = 0;
			int index = 0;
			for (int i = 0; i< population.size(); i++)
			{
				if(best < fitnesses.get(i))
				{
					best = fitnesses.get(i);
					index = i;
				}
			}
			//System.out.println("best fitness is "+ best);
			return index;
		}

		public double GetBestScore(){
			double best = 0;
			int index = 0;
			for (int i = 0; i< population.size(); i++)
			{
				if(best < fitnesses.get(i))
				{
					best = fitnesses.get(i);
					index = i;
				}
			}
			//System.out.println("best fitness is "+ best);
			return best;
		}

		public double GetWorstSolution(){
			double worst = 1;
			int index = 0;
			for (int i = 0; i< population.size(); i++)
			{
				if(worst > fitnesses.get(i))
				{
					worst = fitnesses.get(i);
					index = i;
				}
			}
			//System.out.println("worst fitness is "+ worst);
			return worst;
		}

    public void runSimulation() {
			//createPopulation();
			//for i in range(numberEpochs)
			int g = 0;
			while (true)
			{
				getFitnesses();
				NewPopulation();
				if (g % 20 == 0)
				{
					if (g % 40 == 0)
					{
						double score = GetAverageFitness();
						//int worst = GetWorstSolution();
						//System.out.println( g + " average: " + score + " worst: " + worst);
						System.out.println( g + " average: " + score + " best: " + GetBestScore() );
					}
					canvas.setImage(population.get(GetBestSolution()));
					//canvas.paintComponent(canvas.getGraphics());
					canvas.repaint();
				}
				g += 1;
				if (MUTATION_RATE > MIN_MUTATE)
					MUTATION_RATE = MUTATION_RATE * .99;
			}

    }

    public static void main(String[] args) throws IOException {

        String realPictureFilename = "test.jpg";

        BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));

        JFrame frame = new JFrame();
        frame.setSize(realPicture.getWidth(), realPicture.getHeight());
        frame.setTitle("GA Simulation of Art");

        GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
        frame.add(theCanvas);
        frame.setVisible(true);

        GA pt = new GA(theCanvas, realPicture);
            pt.runSimulation();
    }
}
