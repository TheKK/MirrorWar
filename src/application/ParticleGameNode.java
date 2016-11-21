package application;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.scene.canvas.GraphicsContext;

public class ParticleGameNode extends GameNode {
	class ParticleData {
		Rectangle2D.Double geometry = new Rectangle2D.Double();
		long lifeLimit = 0;
		long age = 0;
		
		public boolean timePassAndCheckIfDie(long elapse) {
			age += elapse;
			if (age >= lifeLimit) {
				return true;
			} else {
				return false;
			}
		}
	}

	SpriteGameNode particleImage;
	ArrayList<ParticleData> particles;
	HashSet<ParticleData> usedParticles;
	HashSet<ParticleData> availableParticles;
	
	long lifeLimit = 1000;
	
	double particleWidth = 0;
	double particleHeight = 0;

	public ParticleGameNode(SpriteGameNode particleImage, int particleNum) {
		this.particleImage = particleImage;

		particles = new ArrayList<>(particleNum);
		usedParticles = new HashSet<>();
		availableParticles = new HashSet<>();
		
		particleWidth = particleImage.geometry.width;
		particleHeight = particleImage.geometry.height;
		
		for (int i = 0; i < particleNum; ++i) {
			particles.add(new ParticleData());
		}

		availableParticles.addAll(particles);
	}
	
	public void emit(double x, double y) {
		if (availableParticles.isEmpty()) {
			return;
		}
		
		ParticleData p = availableParticles.iterator().next();
		usedParticles.add(p);
		availableParticles.remove(p);
		
		p.geometry.setFrame(x, y, particleWidth, particleHeight);
		p.age = 0;
		p.lifeLimit = lifeLimit;
	}

	@Override
	public void update(long elapse) {
		final ArrayList<ParticleData> deadParticles = new ArrayList<>();

		for (ParticleData p: usedParticles) {
			if (p.timePassAndCheckIfDie(elapse)) {
				deadParticles.add(p);
			}
		}
		
		for (ParticleData p: deadParticles) {
			usedParticles.remove(p);
			availableParticles.add(p);
		}
		
		deadParticles.clear();
	}
	
	@Override
	public void render(GraphicsContext gc) {
		for (ParticleData p: usedParticles) {
			particleImage.geometry.setFrame(p.geometry);
			particleImage.render(gc);
		}
	}
}