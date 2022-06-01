package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.FormulaData;
import com.example.demo.service.DateCalculationService;

@Controller
@RequestMapping("/calculation")
public class DateCalculationController {
    private final DateCalculationService dateCalculationService;

    public DateCalculationController(DateCalculationService dateCalculationService) {
	this.dateCalculationService = dateCalculationService;
    }

    @GetMapping("/top")
    public String getFormulaData(Model model) {
	// DBに入っている既存の計算式を表示させる
	List<FormulaData> formulaDataList = dateCalculationService.getAll();
	System.out.println(formulaDataList);
	if (CollectionUtils.isEmpty(formulaDataList)) {
	    String error = "登録されている計算式はありません。";
	    model.addAttribute("complete", error);
	}
	model.addAttribute("fdList", formulaDataList);
	return "calculation/top";
    }

    @PostMapping("/top")
    public String postTop(@RequestParam("inputDate") String inputDate, Model model) {
	// 入力日が空の場合はエラーを出す
	if (inputDate.isEmpty()) {
	    String error = "＊基準日を入力して下さい。";
	    model.addAttribute("inputError", error);
	    inputDate = "　";
	    model.addAttribute("id", inputDate);
	    // 計算式の取得表示
	    List<FormulaData> formulaDataList = dateCalculationService.getAll();
	    model.addAttribute("fdList", formulaDataList);

	    return "calculation/top";
	}
	// 入力日がある場合は計算処理を行い結果出力する
	List<LocalDate> resultList = dateCalculationService.dateAdjust(inputDate);
	System.out.println(resultList);
	List<String> resultList2 = new ArrayList<String>();

	// LocalDateリストから取り出してyyyy/MM/ddのフォーマットにする
	for (LocalDate result : resultList) {
	    String result2 = result.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	    // Stringリストに戻す
	    resultList2.add(result2);
	}
	model.addAttribute("resultList", resultList2);

	// 入力された基準日の表示
	String inputDate2 = inputDate.replace("-", "/");
	model.addAttribute("id", inputDate2);
	// 計算式の取得表示
	List<FormulaData> formulaDataList = dateCalculationService.getAll();
	System.out.println(formulaDataList);
	model.addAttribute("fdList", formulaDataList);

	return "calculation/top";
    }

//「新規登録」押下後の画面取得  
    @GetMapping("/new")
    public String form(FormulaData formulaData, Model model) {
	model.addAttribute("formuladata", new FormulaData());
	return "calculation/new";
    }

//確認画面から戻った時
    @PostMapping("/new")
    public String formback(FormulaData formulaData, Model model) {
	return "calculation/new";
    }

//新規登録にて「次へ」押下時　バリデーションチェック行なう
    @PostMapping("/new_confirm")
    public String confirm(@Validated FormulaData formulaData, BindingResult result, Model model) {
	if (result.hasErrors()) {
	    return "calculation/new";
	}
	// 入力エラーなければ確認画面へ進む
	return "calculation/new_confirm";

    }

//確認画面にて「登録する」押下時　エラーがなければDBに新規登録してnew画面に戻る
    @PostMapping("/complete")
    public String create(@Validated FormulaData formulaData, BindingResult result, Model model,
	    RedirectAttributes redirectAttributes) {

	if (result.hasErrors()) {
	    redirectAttributes.addFlashAttribute("complete", "新規登録失敗しました。");
	    return "calculation/new";
	}
	dateCalculationService.insertOne(formulaData);
	redirectAttributes.addFlashAttribute("complete", "新規登録完了しました。");
	return "redirect:/calculation/top";
    }

    // top.htmlにて[更新]押下時にchange.htmlを表示
    @GetMapping("/change/id={id}")
    public String change(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
	System.out.println(dateCalculationService.getOne(id));

	Optional<FormulaData> fd = dateCalculationService.getOne(id);
	if (fd.isEmpty()) {
	    System.out.println("値がnullです");
	    redirectAttributes.addFlashAttribute("complete", "存在しないIDです。");
	    return "redirect:/calculation/top";
	}
	FormulaData fd2 = fd.get();
	model.addAttribute("formulaData", fd2);
	return "calculation/change";
    }

    // change.htmlにて[次へ]押下時 バリデーションチェック行なう
    @PostMapping("/change_confirm")
    public String changeConfirm(@Validated FormulaData formulaData, BindingResult result, Model model) {
	if (result.hasErrors()) {
	    return "calculation/change";
	}
	// 入力エラーなければ確認画面へ進む
	return "calculation/change_confirm";
    }

    // 確認画面から戻った時
    @PostMapping("/change/id={id}")
    public String changeback(FormulaData formulaData, Model model) {
	return "calculation/change";
    }

//確認画面にて「更新する」押下時　エラーがなければDBに更新登録してchnge.htmlに戻る
    @PostMapping("/change_complete")
    public String upgate(@Validated FormulaData formulaData, BindingResult result, Model model,
	    RedirectAttributes redirectAttributes) {

	if (result.hasErrors()) {
	    redirectAttributes.addFlashAttribute("complete", "更新失敗しました。");
	    return "calculation/change";
	}

	dateCalculationService.updateOne(formulaData.getId(), formulaData.getName(), formulaData.getDetail(),
		formulaData.getYear(), formulaData.getMonth(), formulaData.getDay());
	redirectAttributes.addFlashAttribute("complete", "更新完了しました。");
	return "redirect:/calculation/top";
    }

    // calculation.htmlにて[削除]押下

    @PostMapping("delete/id={id}")
    public String delete(@PathVariable int id, @ModelAttribute FormulaData formulaData,
	    RedirectAttributes redirectAttributes) {
	dateCalculationService.deleteOne(formulaData);
	redirectAttributes.addFlashAttribute("complete", "削除完了しました。");
	return "redirect:/calculation/top";
    }

}