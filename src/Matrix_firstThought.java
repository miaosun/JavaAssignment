//import java.util.ArrayList;
//import java.util.Vector;
//
//
//public class Matrix_firstThought {
//	private ArrayList<Vector<Double>> matrix;
//
//	public Matrix_firstThought() {
//		matrix = new ArrayList<Vector<Double>>();
//	}
//	
//	public Matrix_firstThought(int n) {
//		matrix = new ArrayList<>(n);
//	}
//
//	public ArrayList<Vector<Double>> getMatrix() {
//		return matrix;
//	}
//
//	public void setMatrix(ArrayList<Vector<Double>> matrix2) {
//		this.matrix = matrix2;
//	}
//
//	public Vector<Double> mult(Vector<Double> v) {
//		if(v.size() != matrix.get(0).size())
//			throw new IllegalArgumentException("matrix size do not match");
//
//		Vector<Double> res = new Vector<Double>(v.size());
//		double prod = 0.0;
//		for(int i=0; i<matrix.size(); i++)
//		{
//			for(int j=0; j<v.size(); j++)
//			{
//				prod += matrix.get(i).get(j) * v.get(j);
//			}
//			res.add(prod);
//		}
//		return res;
//	}
///*
//	public Matrix inverse()
//	{
//		if(this.CalcDeterminant() == 0)	throw new IllegalArgumentException("matrix which has determinant 0 cannot be inversed");
//
//		Matrix p = new Matrix();
//		p.setMatrix(this.reorder());
//		//std::cout << "p matrix:\n" << p;
//
//		MathMatrix pa(p*(*this));
//		//std::cout << "pa matrix:\n" << pa;
//
//		MathMatrix l(n), u(n);
//		l = compute_lower();
//		//std::cout << "L matrix:\n" << l;
//
//		u = compute_upper();
//		//std::cout << "U matrix:\n" << u;
//
//		MathMatrix inv(n);
//		MathVector b(n), x(n);
//
//		for(int i=0; i<n; i++)
//		{
//			b[i] = 1.0;
//			x = lu_solve(l, u, b);
//			for(int j=0; j<n; j++)
//				inv(j,i) = x[j];
//			b[i] = 0.0;
//		}
//		return inv;
//	}
//*/
//	
//	 public Matrix_firstThought reorder()
//	 {
//	 	Matrix_firstThought p = new Matrix_firstThought();
//	 	int i, j, k;
//	 	Vector<Integer> pvt = new Vector<Integer>();
//	 	int pvtk, pvti;
//	 	Vector<Integer> scale = new Vector<Integer>();
//	 	double aet, tmp, mult;
//	 	Matrix_firstThought temp = new Matrix_firstThought();
//	 	temp.setMatrix(matrix);
//
//	 	int n = matrix.size();
//	 	for(k=0; k<n; k++)
//	 		pvt.set(k, k);
//
//	 	for(k=0; k<n; k++)
//	 	{
//	 		scale.set(k, 0);
//	 		for(j=0; j<n; j++)
//	 		{
//	 			if(Math.abs(scale.get(k)) < Math.abs(temp.getVectorByIndex(k).get(j)))
//	 				scale.set(k, (int)Math.abs(temp.getVectorByIndex(k).get(j)));
//	 		}
//	 	}
//
//	 	for(k=0; k<n-1; k++)
//	 	{
//	 		int pc = k;
//	 		
//	 		aet = Math.abs(temp.getVectorByIndex(pvt.get(k)).get(k)/scale.get(k));
//	 		for(i=k+1; i<n; i++)
//	 		{
//	 			tmp = Math.abs(Math.abs(temp.getVectorByIndex(pvt.get(i)).get(k)/scale.get(pvt.get(i))));
//	 			if(tmp > aet)
//	 			{
//	 				aet = tmp;
//	 				pc = i;
//	 			}
//	 		}
//
//	 		if(aet == 0)
//	 		{
//	 			System.out.println("pivot is zero\n");
//	 			System.exit(1);
//	 		}
//	 		if(pc != k)
//	 		{
//	 			int ii = pvt.get(k);
//	 			pvt.set(k, pvt.get(pc));
//	 			pvt.set(pc, ii);
//	 		}
//
//	 		pvtk = pvt.get(k);
//	 		for(i=k+1; i<n; i++)
//	 		{
//	 			pvti = pvt.get(i);
//	 			if(temp.getVectorByIndex(pvti).get(k) != 0)
//	 			{
//	 				mult = temp.getVectorByIndex(pvti).get(k)/temp.getVectorByIndex(pvtk).get(k);
//	 				temp.addValue(mult, pvti, k);
//	 				for(j=k+1; j<n; j++)
//	 					temp.addValue(temp.getVectorByIndex(pvti).get(j) - mult*temp.getVectorByIndex(pvtk).get(j), pvti, j);
//	 			}
//	 		}
//	 	}
//	 	for(i=0; i<n; i++)
//	 		p.addValue(1.0, i, pvt.get(i));
//
//	 	return p;
//	 }
//	
//	public void addValue(double value, int i, int j)
//	{
//		Vector<Double> v=new Vector<Double>(0);
//		v.add(j, value);
//		matrix.add(i, v);
//	}
//	
//	public void addVector(Vector<Double> v)
//	{
//		matrix.add(v);
//	}
//	
//	public Vector<Double> getVectorByIndex(int index)
//	{
//		return matrix.get(index);
//	}
//	
//	public String toString()
//	{
//		String res = "";
//		for(int i=0; i<matrix.size(); i++)
//		{
//			for(int j=0; j<matrix.get(i).size(); j++)
//			{
//				res += matrix.get(i).get(j) + "  ";
//			}
//			res += "\n";
//		}
//		return res;
//	}
//
//	public double CalcDeterminant()
//	{
//		int c, subi, i, j, subj;
//		double d = 0.0;
//		Matrix_firstThought submat = new Matrix_firstThought();
//		if (matrix.size() == 2) {
//			return(this.matrix.get(0).get(0) * this.matrix.get(1).get(1)) - (this.matrix.get(0).get(1) * this.matrix.get(1).get(0));
//		}
//		else
//		{
//			for (c = 0; c < matrix.size(); c++) {
//				subi = 0;
//				for (i = 1; i < matrix.size(); i++) {
//					subj = 0;
//					for (j = 0; j < matrix.size(); j++) {
//						if (j == c) {
//							continue;
//						}
//						submat.addValue(this.matrix.get(i).get(j), subi, subj);
//						subj++;
//					}
//					subi++;
//				}
//				d = d + (Math.pow(-1, c) * this.matrix.get(0).get(c) * submat.CalcDeterminant());
//			}
//		}
//		return d;
//	}
//}
