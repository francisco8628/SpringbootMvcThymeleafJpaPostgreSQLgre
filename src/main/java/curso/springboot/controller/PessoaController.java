package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
  
  @Autowired	
  private PessoaRepository pessoaRepository;	 /*injeção da pessoa dependencia*/
  
  @Autowired
  private TelefoneRepository telefoneRepository;
	
	
  @RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
  public ModelAndView inicio() {
	  ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
 	  modelAndView.addObject("pessoaobj",new Pessoa());	   
	  Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
	  modelAndView.addObject("pessoas", pessoaIt);	
	  return modelAndView;
  }
  
 // @RequestMapping(method = RequestMethod.POST, value = "savarpessoa" ) /*intercptando a url*/
  //public String salvar(Pessoa pessoa){                                  /*injetando no objeto*/
	 
	// pessoaRepository.save(pessoa);                                  /*salvando */
	 //return "cadastro/cadastropessoa";                               /*rtornando para a tela*/
 // }  salva e retorna para a tela sem listas
  
  @RequestMapping(method = RequestMethod.POST, value = "**/savarpessoa" ) /*intercptando a url*/
  public ModelAndView salvarPessoas(@Valid Pessoa pessoa, BindingResult bindingResult){                                  /*injetando no objeto*/
	  
	  if(bindingResult.hasErrors()) {
		  ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");		
		  Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		  modelAndView.addObject("pessoas", pessoaIt);  //se ainda tiver erro mostra os dados anteriores na tela
		  modelAndView.addObject("pessoaobj",pessoa);
		  
		  List<String> msg = new ArrayList<String>();
		  
		  for(ObjectError objectError : bindingResult.getAllErrors()) {
			  msg.add(objectError.getDefaultMessage());
		  }
		  
		  modelAndView.addObject("msg",msg); //retora uma lista de mensagem
		  return modelAndView;
	  }
	  
	  pessoaRepository.save(pessoa);     
	  
	  ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
	  Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
	  andView.addObject("pessoas", pessoaIt);
	  andView.addObject("pessoaobj",new Pessoa());
	  
	  return andView;
  }
  
  @RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
  public ModelAndView listaPessoas() {
	  ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
	  Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
	  andView.addObject("pessoas", pessoaIt);
	  andView.addObject("pessoaobj",new Pessoa());
	  return andView;
	  
  }
  
  @GetMapping("/editarpessoa/{idpessoa}")
  public ModelAndView editarPesssoa(@PathVariable("idpessoa")Long idpessoa) {
	
	  Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

	  ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
	  modelAndView.addObject("pessoaobj",pessoa.get());	  
	  return modelAndView;
}
  
  @GetMapping("/removerpessoa/{idpessoa}")
  public ModelAndView removerPesssoa(@PathVariable("idpessoa")Long idpessoa) {
	
	  pessoaRepository.deleteById(idpessoa);

	  ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //fala a tela que deve retornar
	  modelAndView.addObject("pessoas",pessoaRepository.findAll());//atualiza a tela sem o objeto excluido	  
	  modelAndView.addObject("pessoaobj",new Pessoa());
	  return modelAndView;
}
  
  @PostMapping("**/pesquisapessoa")
  public ModelAndView pesuisarPessoa(@RequestParam("nomepesquisa") String nomepesquisa) {
   ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
   modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
   modelAndView.addObject("pessoaobj",new Pessoa());
   
   return modelAndView;
  }
  
  @GetMapping("/telefones/{idpessoa}")   //intercepta a url
  public ModelAndView telefones(@PathVariable("idpessoa")Long idpessoa) {
	
	  Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

	  ModelAndView modelAndView = new ModelAndView("/cadastro/telefones");//retorna para a tela de telefones
	  modelAndView.addObject("pessoaobj",pessoa.get());	
	  modelAndView.addObject("telefones",telefoneRepository.getTelefones(idpessoa));
	  return modelAndView;
}
  @PostMapping("**/addfonePessoa/{pessoaid}")
  public ModelAndView addFonePessoa(Telefone telefone,@PathVariable("pessoaid")Long pessoaid){
	 	  	  
	  Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
	  
	  if(telefone!=null && telefone.getNumero().isEmpty() 
			  || telefone.getTipo().isEmpty()) {
		  
		  ModelAndView modelAndView = new ModelAndView("cadastro/telefones");//retorna para a tela de telefones	  
		  modelAndView.addObject("pessoaobj",pessoa);
		  modelAndView.addObject("telefones",telefoneRepository.getTelefones(pessoaid));  
		  
		   List<String> msg = new  ArrayList<String>();
		   
		   if(telefone.getNumero().isEmpty()) {
		   msg.add("O numero deve ser informado");
		   modelAndView.addObject("msg",msg);
		   }
		   
		   if(telefone.getTipo().isEmpty()) {
			   msg.add("O Tipo deve ser informado");
			   modelAndView.addObject("msg",msg); 
		   }
		   return modelAndView;
	  }
	  
	  
	  telefone.setPessoa(pessoa);
	  
	  telefoneRepository.save(telefone);
	  
	  ModelAndView modelAndView = new ModelAndView("cadastro/telefones");//retorna para a tela de telefones	  
	  modelAndView.addObject("pessoaobj",pessoa);
	  modelAndView.addObject("telefones",telefoneRepository.getTelefones(pessoaid));                           	  
	  return modelAndView;
	  
  }
  /*metodoto para remover telefone*/
  @GetMapping("/removertelefone/{idtelefone}")
  public ModelAndView removerTelefone(@PathVariable("idtelefone")Long idtelefone) {
	  
	  Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
	  
	  telefoneRepository.deleteById(idtelefone);

	  ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //fala a tela que deve retornar
	  modelAndView.addObject("pessoaobj",pessoa);
	  modelAndView.addObject("telefones",telefoneRepository.getTelefones(pessoa.getId()));
	  return modelAndView;
}
  
  
}
