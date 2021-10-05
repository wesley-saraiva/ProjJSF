package br.com.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

@Named
public class IDaoPessoaImpl implements IDaoPessoa {

	@Inject
	private JPAUtil jpaUtil;

	@Inject
	private EntityManager entityManager;

	@Override
	public Pessoa consultarUsuario(String login, String senha) {

		Pessoa pessoa = null;

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		try {
			pessoa = (Pessoa) entityManager
					.createQuery("select p from Pessoa p where p.login = '" + login + "' and p.senha = '" + senha + "'")
					.getSingleResult();
		} catch (javax.persistence.NoResultException e) {// tratamento se nao encontrar usuario e senha
			// TODO: handle exception
		}
		entityTransaction.commit();

		return pessoa;
	}

	@Override
	public List<SelectItem> listasEstados() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();

		for (Estados estado : estados) {
			selectItems.add(new SelectItem(estado, estado.getNome()));
		}

		return selectItems;
	}

	@Override
	public List<Pessoa> relatorioPessoa(String nome, Date dataIni, Date dataFin) {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();

		StringBuilder sql = new StringBuilder();

		sql.append(" select ps from Pessoa ps");

		if (dataIni == null && dataFin == null && nome != null && !nome.isEmpty()) {
			sql.append(" where upper(ps.nome) like '%").append(nome.trim().toUpperCase()).append("%'");

		} else if (nome == null || (nome != null && nome.isEmpty()) && dataIni != null && dataFin == null) {

			String dataNascString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			sql.append(" where ps.dataNascimento >= '").append(dataNascString).append("'");
			
		} else if (nome == null || (nome != null && nome.isEmpty()) && dataIni == null && dataFin != null) {

			String dataNascString = new SimpleDateFormat("yyyy-MM-dd").format(dataFin);
			sql.append(" where ps.dataNascimento <= '").append(dataNascString).append("'");	

		} else if (nome != null && !nome.isEmpty() && dataIni != null && dataFin != null) {

			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String dataFinString = new SimpleDateFormat("yyyy-MM-dd").format(dataFin);

			sql.append(" where ps.dataNascimento >= '").append(dataIniString).append("'");
			sql.append(" and ps.dataNascimento <= '").append(dataFinString).append("'");
			sql.append(" and upper(ps.nome) like '%").append(nome.trim().toUpperCase()).append("%'");
		}

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		pessoas = entityManager.createQuery(sql.toString()).getResultList();
		
		entityTransaction.commit();

		return pessoas;
	}

}
