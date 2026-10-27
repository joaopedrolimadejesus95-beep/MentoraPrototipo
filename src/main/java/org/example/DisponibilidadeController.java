package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DisponibilidadeController {
    private final DisponibilidadeDAO dao = new DisponibilidadeDAO();
    private static final Map<DayOfWeek,String> NOMES = Map.of(DayOfWeek.MONDAY,"Segunda",DayOfWeek.TUESDAY,"Terça",DayOfWeek.WEDNESDAY,"Quarta",DayOfWeek.THURSDAY,"Quinta",DayOfWeek.FRIDAY,"Sexta",DayOfWeek.SATURDAY,"Sábado",DayOfWeek.SUNDAY,"Domingo");
    @GetMapping("/horarios") public String tela(Model m) {
        try { m.addAttribute("form", montar(dao.listarSemana())); }
        catch(SQLException e) { m.addAttribute("form", montar(List.of())); m.addAttribute("erroBanco","Execute sql-disponibilidade.sql e confira a conexão."); }
        return "horarios";
    }
    @PostMapping("/horarios") public String salvar(@ModelAttribute("form") HorariosSemanaForm f, RedirectAttributes r) {
        try { dao.substituirSemana(validar(f)); r.addFlashAttribute("sucessoAcao","Grade semanal salva."); }
        catch(IllegalArgumentException e) { r.addFlashAttribute("erroAcao",e.getMessage()); }
        catch(SQLException e) { r.addFlashAttribute("erroAcao","Execute sql-disponibilidade.sql e tente novamente."); }
        return "redirect:/horarios";
    }
    private HorariosSemanaForm montar(List<IntervaloDisponivel> itens) {
        Map<DayOfWeek,List<IntervaloDisponivel>> mapa=itens.stream().collect(Collectors.groupingBy(IntervaloDisponivel::getDia));
        HorariosSemanaForm f=new HorariosSemanaForm(); List<DiaDisponivelForm> linhas=new ArrayList<>();
        for(DayOfWeek d:DayOfWeek.values()) { DiaDisponivelForm x=new DiaDisponivelForm(); x.setDiaSemana(d.name()); x.setNome(NOMES.get(d)); List<IntervaloDisponivel> l=mapa.getOrDefault(d,List.of());
            if(l.size()>0){x.setInicio1(l.get(0).getInicio().toString());x.setFim1(l.get(0).getFim().toString());} if(l.size()>1){x.setInicio2(l.get(1).getInicio().toString());x.setFim2(l.get(1).getFim().toString());} linhas.add(x); }
        f.setDias(linhas);return f;
    }
    private List<IntervaloDisponivel> validar(HorariosSemanaForm f) {
        if(f.getDias()==null||f.getDias().size()!=7)throw new IllegalArgumentException("A grade precisa ter os sete dias."); List<IntervaloDisponivel> out=new ArrayList<>();
        for(int i=0;i<7;i++){DiaDisponivelForm x=f.getDias().get(i);DayOfWeek d=DayOfWeek.values()[i];if(!d.name().equals(x.getDiaSemana()))throw new IllegalArgumentException("Atualize a página e tente novamente.");List<LocalTime[]> h=new ArrayList<>();
            add(x.getInicio1(),x.getFim1(),x.getNome(),h);add(x.getInicio2(),x.getFim2(),x.getNome(),h);h.sort(Comparator.comparing(a->a[0]));if(h.size()==2&&h.get(0)[1].isAfter(h.get(1)[0]))throw new IllegalArgumentException("Os intervalos de "+x.getNome()+" se sobrepõem.");for(LocalTime[] a:h)out.add(new IntervaloDisponivel(d,a[0],a[1]));}
        return out;
    }
    private void add(String a,String b,String dia,List<LocalTime[]> out){boolean ea=a==null||a.isBlank(),eb=b==null||b.isBlank();if(ea&&eb)return;if(ea||eb)throw new IllegalArgumentException("Preencha início e fim do intervalo de "+dia+".");try{LocalTime i=LocalTime.parse(a),f=LocalTime.parse(b);if(!f.isAfter(i))throw new IllegalArgumentException("O fim precisa ser depois do início em "+dia+".");out.add(new LocalTime[]{i,f});}catch(DateTimeParseException e){throw new IllegalArgumentException("Confira os horários de "+dia+".");}}
}
