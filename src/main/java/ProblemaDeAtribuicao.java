import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Scanner;

public class ProblemaDeAtribuicao {

	int n; // numero de trabalhadores e tarefas
	double[][] c; // c_ij corresponde ao custo de atribuir a tarefa "i" ao
					// trabalhador "j"

	void leDadosDaEntradaPadrao() {
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		c = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c[i][j] = in.nextDouble();
			}
		}
		in.close();
	}

	void resolvePI() throws Exception {
		IloCplex cplex = new IloCplex();
		IloNumVar[][] x = new IloNumVar[n][n];

		// adicionando as variaveis no modelo
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				x[i][j] = cplex.boolVar();
			}
		}

		// a cada trabalhador "j" deve ser atribuida uma tarefa
		for (int j = 0; j < n; j++) {
			IloLinearNumExpr expr = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
				expr.addTerm(1.0, x[i][j]);
			}
			cplex.addEq(expr, 1);
		}

		// cada tarefa "i" deve ser atribuida a um trabalhador
		for (int i = 0; i < n; i++) {
			IloLinearNumExpr expr = cplex.linearNumExpr();
			for (int j = 0; j < n; j++) {
				expr.addTerm(1.0, x[i][j]);
			}
			cplex.addEq(expr, 1);
		}

		// minimiza o custo total da atribuicao
		IloLinearNumExpr obj = cplex.linearNumExpr();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				obj.addTerm(c[i][j], x[i][j]);
			}
		}
		cplex.addMinimize(obj);
		
		// pede para o solver resolver a instancia do problema
		cplex.solve();
		
		// imprime a solucao encontrada 
		System.out.println("Status: " + cplex.getStatus());
		double tolerance = cplex.getParam(IloCplex.DoubleParam.EpInt);
		System.out.println("Custo de uma atribuicao otima: "
				+ cplex.getObjValue());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (cplex.getValue(x[i][j]) >= 1 - tolerance) {
					System.out.println("Tarefa " + i
							+ " atribuida ao trabalhador " + j);
				}
			}
		}
		
		// libera a memoria alocada pelo solver 
		cplex.end();
	}
		
	public static void main(String[] args) throws Exception {
		ProblemaDeAtribuicao p = new ProblemaDeAtribuicao();
		p.leDadosDaEntradaPadrao();
		p.resolvePI();
	}
}
