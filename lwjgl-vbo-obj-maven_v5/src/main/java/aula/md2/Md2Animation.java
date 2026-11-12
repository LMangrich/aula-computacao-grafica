package aula.md2;

// controla qual animação está tocando e em qual frame ela está; só faz contas com números, não usa OpenGL
public class Md2Animation {
	int startFrame = 0, endFrame = 39; // primeiro e último frame da animação atual
	float fps = 10; // frames por segundo da animação (o Quake usa 10)
	int currentFrame = 0; // frame que o draw() desenha
	int nextFrame = 0; // frame seguinte, para onde o modelo está indo
	float blend = 0; // t: quanto já andou do frame atual até o seguinte (0 a 1)

	String currentName = null; // nome da animação atual ("run", "stand"...); null = nenhuma escolhida ainda
	double animStartTime = 0; // instante (em segundos) em que a animação atual começou
	boolean restart = false; // true = a animação acabou de trocar e precisa voltar ao primeiro frame

	public void setAnimation(String name, Md2Frame[] frames) { // escolhe a animação pelo nome: "stand", "run", "attack"...
		if (name.equals(currentName)) {
			return; // já é a animação atual: segurar a tecla não reinicia nem imprime de novo
		}
		int first = -1;
		int last = -1;
		for (int i = 0; i < frames.length; i++) {
			String base = frames[i].name.replaceAll("\\d+$", ""); // tira os números do fim: "run3" vira "run"
			if (base.equals(name)) {
				if (first == -1) {
					first = i; // primeiro frame com esse nome
				}
				last = i; // vai atualizando até o último
			}
		}
		if (first == -1) {
			System.out.println("animacao nao encontrada: " + name);
			return; // mantém a animação anterior
		}
		startFrame = first;
		endFrame = last;
		currentName = name;
		restart = true; // o próximo animate() marca o tempo de início
		System.out.println("animacao " + name + " = frames " + startFrame + " a " + endFrame);
	}

	public void animate(double time) { // chamado a cada volta do loop, com o tempo em segundos
		if (restart) { // a animação mudou desde a última chamada
			animStartTime = time; // marca o instante em que ela começou
			restart = false;
		}
		int count = endFrame - startFrame + 1; // quantos frames tem a animação
		double passed = (time - animStartTime) * fps; // frames passados, COM as casas decimais (ex.: 23.4)
		int whole = (int) passed; // parte inteira (23)
		currentFrame = startFrame + (whole % count); // frame de onde partimos
		nextFrame = startFrame + ((whole + 1) % count); // frame para onde vamos; no fim da animação volta ao primeiro
		blend = (float) (passed - whole); // parte depois da vírgula (0.4): é o t da interpolação
	}
}
